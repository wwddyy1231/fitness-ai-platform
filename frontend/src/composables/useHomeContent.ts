import { onScopeDispose, ref, shallowRef } from 'vue'

import { getHome, type HomeDto } from '@/api/home'
import { isHomeViewModelEmpty, mapHomeDto, type HomeViewModel } from '@/api/home/mapper'
import { toApiClientError } from '@/api/error'

export type HomeRequestStatus = 'idle' | 'loading' | 'success' | 'empty' | 'error'
export type HomeFetcher = (signal?: AbortSignal) => Promise<HomeDto>

export function useHomeContent(fetchHome: HomeFetcher = getHome) {
  const status = ref<HomeRequestStatus>('idle')
  const data = shallowRef<HomeViewModel | null>(null)
  const errorMessage = ref('')
  let activeController: AbortController | null = null
  let requestSequence = 0

  async function load(): Promise<void> {
    activeController?.abort()
    const controller = new AbortController()
    activeController = controller
    const requestId = ++requestSequence
    status.value = 'loading'
    errorMessage.value = ''

    try {
      const model = mapHomeDto(await fetchHome(controller.signal))
      if (requestId !== requestSequence) return
      data.value = model
      status.value = isHomeViewModelEmpty(model) ? 'empty' : 'success'
    } catch (error: unknown) {
      if (requestId !== requestSequence || controller.signal.aborted) return
      data.value = null
      errorMessage.value = toApiClientError(error).message
      status.value = 'error'
    } finally {
      if (activeController === controller) activeController = null
    }
  }

  function cancel(): void {
    activeController?.abort()
    activeController = null
  }

  onScopeDispose(cancel)

  return { status, data, errorMessage, load, cancel }
}
