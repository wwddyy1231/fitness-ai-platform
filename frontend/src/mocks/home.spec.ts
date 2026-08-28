import { describe, expect, it } from 'vitest'

import { fitnessCategories, trainingPlans, trendingArticles } from './home'

describe('home mock data', () => {
  it('provides the expected content groups for the first home page', () => {
    expect(fitnessCategories).toHaveLength(6)
    expect(trainingPlans.map((plan) => plan.level)).toEqual(['初级', '中级', '高级'])
    expect(trendingArticles).toHaveLength(5)
  })

  it('uses stable unique ids within each content group', () => {
    expect(new Set(fitnessCategories.map((category) => category.id)).size).toBe(
      fitnessCategories.length,
    )
    expect(new Set(trainingPlans.map((plan) => plan.id)).size).toBe(trainingPlans.length)
  })
})
