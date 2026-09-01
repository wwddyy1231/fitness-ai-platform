<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import { useAuthStore } from '@/stores/auth'

interface LoginForm {
  username: string
  password: string
}

const authStore = useAuthStore()
const router = useRouter()
const formRef = ref<FormInstance>()
const form = reactive<LoginForm>({ username: '', password: '' })
const rules: FormRules<LoginForm> = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}
const submitting = computed(() => authStore.status === 'loading')

async function submit(): Promise<void> {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (await authStore.login({ username: form.username.trim(), password: form.password })) {
    await router.replace({ name: 'home' })
  }
}
</script>

<template>
  <section class="login-section" aria-labelledby="login-title">
    <div class="content-container login-layout">
      <div class="login-context">
        <p class="section-kicker">会员账户</p>
        <h1 id="login-title">登录 Fitness AI</h1>
        <p>继续使用你的健身账户和个性化服务。</p>
      </div>

      <div class="login-panel">
        <el-alert
          v-if="authStore.errorMessage"
          class="login-error"
          type="error"
          :title="authStore.errorMessage"
          :closable="false"
          show-icon
        />
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          size="large"
          @submit.prevent="submit"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="form.username"
              name="username"
              autocomplete="username"
              maxlength="32"
              placeholder="请输入用户名"
            />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              name="password"
              type="password"
              autocomplete="current-password"
              maxlength="64"
              show-password
              placeholder="请输入密码"
              @keyup.enter="submit"
            />
          </el-form-item>
          <el-button
            class="login-submit"
            type="primary"
            native-type="submit"
            :loading="submitting"
            :disabled="submitting"
          >
            登录
          </el-button>
        </el-form>
      </div>
    </div>
  </section>
</template>

<style scoped>
.login-section {
  padding: var(--space-16) 0 var(--space-20);
}

.login-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(20rem, 28rem);
  align-items: center;
  gap: var(--space-16);
  max-width: 68rem;
}

.login-context h1 {
  margin-bottom: var(--space-4);
  color: var(--color-text-strong);
  font-family: var(--font-family-display);
  font-size: var(--font-size-3xl);
  line-height: var(--line-height-heading);
}

.login-context p:last-child {
  max-width: 34rem;
  color: var(--color-text-muted);
}

.login-panel {
  padding: var(--space-8);
  background: var(--color-surface);
  border: var(--border-thin);
  border-top: 0.25rem solid var(--color-primary-600);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-md);
}

.login-error {
  margin-bottom: var(--space-5);
}

.login-submit {
  width: 100%;
  margin-top: var(--space-2);
}

@media (max-width: 767px) {
  .login-section {
    padding: var(--space-10) 0 var(--space-16);
  }

  .login-layout {
    grid-template-columns: minmax(0, 1fr);
    gap: var(--space-8);
  }

  .login-panel {
    padding: var(--space-6);
  }
}
</style>
