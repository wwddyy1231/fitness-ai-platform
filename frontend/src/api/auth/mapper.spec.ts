import { describe, expect, it } from 'vitest'

import { mapAuthUserDto, mapLoginDto } from './mapper'
import type { AuthUserDto, LoginDto } from './types'

describe('auth mapper', () => {
  it('maps a login response and preserves a large string ID', () => {
    const result = mapLoginDto(loginDto())

    expect(result.user.id).toBe('9223372036854775807')
    expect(result.user.email).toBeNull()
    expect(result.tokenType).toBe('Bearer')
  })

  it('rejects a numeric user ID at runtime', () => {
    const dto = userDto() as unknown as AuthUserDto
    ;(dto as unknown as { id: number }).id = 9007199254740992

    expect(() => mapAuthUserDto(dto)).toThrow('用户 ID 必须以非空字符串传输')
  })

  it('normalizes nullable and blank email values', () => {
    expect(mapAuthUserDto(userDto({ email: null })).email).toBeNull()
    expect(mapAuthUserDto(userDto({ email: '  ' })).email).toBeNull()
  })
})

function loginDto(): LoginDto {
  return {
    accessToken: 'signed-token',
    tokenType: 'Bearer',
    expiresIn: 7200,
    user: userDto(),
  }
}

function userDto(overrides: Partial<AuthUserDto> = {}): AuthUserDto {
  return {
    id: '9223372036854775807',
    username: 'member',
    nickname: '训练者',
    email: null,
    roles: ['MEMBER'],
    ...overrides,
  }
}
