import kettlebellCoachImage from '@/assets/images/kettlebell-coach.jpg'
import performanceNutritionImage from '@/assets/images/performance-nutrition.jpg'
import strengthSquatImage from '@/assets/images/strength-squat.jpg'
import type {
  ArticleSummary,
  FitnessCategory,
  FitnessVideo,
  TrainingPlan,
  TrendingArticle,
} from '@/types/content'

const strengthImage = {
  src: strengthSquatImage,
  alt: '运动员在深蹲架内进行杠铃深蹲训练',
  width: 1600,
  height: 1067,
} as const

const nutritionImage = {
  src: performanceNutritionImage,
  alt: '包含鸡胸肉、糙米、鸡蛋和蔬菜的高蛋白训练餐',
  width: 1600,
  height: 1067,
} as const

const kettlebellImage = {
  src: kettlebellCoachImage,
  alt: '教练在训练室演示壶铃硬拉动作',
  width: 1600,
  height: 918,
} as const

export const featuredArticle: ArticleSummary = {
  id: 'featured-squat-guide',
  category: '力量训练',
  title: '从动作质量到训练容量：重新理解深蹲进阶',
  summary: '用可执行的动作检查、负荷安排与恢复策略，让深蹲真正服务于长期力量增长。',
  publishedAt: '2026-08-26',
  readCount: 12840,
  tags: [],
  image: strengthImage,
}

export const featuredRecommendations: ArticleSummary[] = [
  {
    id: 'protein-timing',
    category: '健身营养',
    title: '蛋白质怎么吃：训练日的三餐分配思路',
    summary: '把总量、质量和进餐节奏放回同一张计划表。',
    publishedAt: '2026-08-26',
    readCount: 8321,
    tags: [],
    image: nutritionImage,
  },
  {
    id: 'kettlebell-basics',
    category: '动作教程',
    title: '壶铃硬拉入门：先学会稳定，再追求速度',
    summary: '从髋铰链、呼吸到握法，建立可靠的动作起点。',
    publishedAt: '2026-08-25',
    readCount: 6749,
    tags: [],
    image: kettlebellImage,
  },
  {
    id: 'recovery-score',
    category: '训练恢复',
    title: '今天适合加重量吗？用四个信号判断恢复状态',
    summary: '不要只看意志力，建立一套简单的训练前自检。',
    publishedAt: '2026-08-24',
    readCount: 5886,
    tags: [],
    image: strengthImage,
  },
  {
    id: 'meal-prep',
    category: '饮食计划',
    title: '一小时准备三天训练餐：份量与保存指南',
    summary: '适合工作日执行的备餐框架，不牺牲口感和营养。',
    publishedAt: '2026-08-23',
    readCount: 5194,
    tags: [],
    image: nutritionImage,
  },
]

export const trendingArticles: TrendingArticle[] = [
  { id: 'trend-1', category: '增肌', title: '训练量越大越好吗？理解有效组与恢复上限' },
  { id: 'trend-2', category: '减脂', title: '体重不变但围度下降，计划是否仍然有效' },
  { id: 'trend-3', category: '器材', title: '家庭力量训练：可调哑铃选购的五个判断标准' },
  { id: 'trend-4', category: '营养', title: '碳水不是敌人：高强度训练前后的摄入策略' },
  { id: 'trend-5', category: '恢复', title: '睡眠不足时，怎样调整当天的训练强度' },
]

export const latestArticles: ArticleSummary[] = [
  {
    id: 'latest-1',
    category: '肌肉健身',
    title: '卧推卡在胸口？先检查肩胛位置与杠铃轨迹',
    summary: '从稳定支点、触胸位置到发力顺序，拆解常见的卧推停滞原因。',
    publishedAt: '2026-08-26',
    readCount: 4268,
    tags: [],
    image: strengthImage,
  },
  {
    id: 'latest-2',
    category: '健身营养',
    title: '训练后没有食欲，如何完成恢复所需营养',
    summary: '用更容易入口的食物组合补足蛋白质、碳水和水分。',
    publishedAt: '2026-08-26',
    readCount: 3512,
    tags: [],
    image: nutritionImage,
  },
  {
    id: 'latest-3',
    category: '居家训练',
    title: '只有一只壶铃，也能完成的全身力量循环',
    summary: '四个基础动作，兼顾下肢、推拉与核心稳定。',
    publishedAt: '2026-08-25',
    readCount: 7190,
    tags: [],
    image: kettlebellImage,
  },
  {
    id: 'latest-4',
    category: '训练计划',
    title: '每周练三天：全身训练与上下肢分化怎么选',
    summary: '根据训练经验、恢复时间和目标选择更可持续的结构。',
    publishedAt: '2026-08-24',
    readCount: 6023,
    tags: [],
    image: strengthImage,
  },
]

export const fitnessCategories: FitnessCategory[] = [
  {
    id: 'muscle',
    slug: 'muscle',
    name: '增肌',
    description: '训练容量与渐进超负荷',
    articleCount: 128,
    icon: 'muscle',
  },
  {
    id: 'fat-loss',
    slug: 'fat-loss',
    name: '减脂',
    description: '饮食、训练与行为习惯',
    articleCount: 96,
    icon: 'fat-loss',
  },
  {
    id: 'strength',
    slug: 'strength',
    name: '力量训练',
    description: '基础动作与力量周期',
    articleCount: 154,
    icon: 'strength',
  },
  {
    id: 'home',
    slug: 'home',
    name: '居家训练',
    description: '有限器械的高效方案',
    articleCount: 72,
    icon: 'home',
  },
  {
    id: 'nutrition',
    slug: 'nutrition',
    name: '营养',
    description: '表现、恢复与体重管理',
    articleCount: 113,
    icon: 'nutrition',
  },
  {
    id: 'equipment',
    slug: 'equipment',
    name: '健身器材',
    description: '选购、使用与维护',
    articleCount: 61,
    icon: 'equipment',
  },
]

export const trainingPlans: TrainingPlan[] = [
  {
    id: 'plan-beginner',
    level: '初级',
    title: '零基础全身力量计划',
    duration: '6 周',
    frequency: '每周 3 练',
    focus: '动作模式与稳定训练习惯',
  },
  {
    id: 'plan-intermediate',
    level: '中级',
    title: '上下肢力量与增肌计划',
    duration: '8 周',
    frequency: '每周 4 练',
    focus: '力量表现与训练容量同步提升',
  },
  {
    id: 'plan-advanced',
    level: '高级',
    title: '复合动作专项强化计划',
    duration: '10 周',
    frequency: '每周 5 练',
    focus: '周期化负荷与薄弱环节补强',
  },
]

export const fitnessVideos: FitnessVideo[] = [
  {
    id: 'video-1',
    category: '动作教学',
    title: '壶铃硬拉：建立正确髋铰链',
    duration: '08:24',
    image: kettlebellImage,
  },
  {
    id: 'video-2',
    category: '力量训练',
    title: '深蹲热身：从踝关节到核心',
    duration: '11:08',
    image: strengthImage,
  },
  {
    id: 'video-3',
    category: '健身营养',
    title: '高蛋白训练餐的快速准备',
    duration: '06:42',
    image: nutritionImage,
  },
  {
    id: 'video-4',
    category: '居家训练',
    title: '单壶铃全身训练跟练',
    duration: '18:15',
    image: kettlebellImage,
  },
]
