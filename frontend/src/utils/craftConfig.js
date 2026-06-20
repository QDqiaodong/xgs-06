export const CRAFT_STYLES = {
  cutting: { name: '裁切', icon: '✂️', tagType: 'danger' },
  sewing: { name: '缝制', icon: '🧵', tagType: 'primary' },
  edge: { name: '封边', icon: '🎨', tagType: 'warning' },
  hardware: { name: '五金安装', icon: '🔩', tagType: 'success' },
  shaping: { name: '塑形', icon: '✨', tagType: 'default' },
  carving: { name: '皮雕', icon: '🗡️', tagType: 'default' },
  weaving: { name: '编织', icon: '🧶', tagType: 'default' },
  punch: { name: '打孔', icon: '⚡', tagType: 'default' },
  coloring: { name: '上色', icon: '🖌️', tagType: 'default' },
  thinning: { name: '削薄', icon: '📏', tagType: 'default' },
  other: { name: '其他', icon: '📝', tagType: 'default' }
}

export const DIFFICULTY_LEVELS = {
  beginner: {
    key: 'beginner',
    name: '入门',
    icon: '🌱',
    tagType: 'success',
    color: '#52c41a',
    bgColor: '#f6ffed',
    borderColor: '#b7eb8f',
    maxSteps: 3,
    description: '适合新手入门，步骤简洁明了'
  },
  intermediate: {
    key: 'intermediate',
    name: '进阶',
    icon: '🔥',
    tagType: 'warning',
    color: '#fa8c16',
    bgColor: '#fff7e6',
    borderColor: '#ffd591',
    maxSteps: 7,
    description: '需要一定基础，步骤较为丰富'
  },
  advanced: {
    key: 'advanced',
    name: '复杂',
    icon: '💎',
    tagType: 'danger',
    color: '#f5222d',
    bgColor: '#fff1f0',
    borderColor: '#ffa39e',
    maxSteps: Infinity,
    description: '高阶作品，工序复杂且技巧性强'
  }
}

export const DIFFICULTY_ORDER = ['beginner', 'intermediate', 'advanced']

export const getSuggestedDifficultyBySteps = (stepCount) => {
  if (!stepCount || stepCount <= DIFFICULTY_LEVELS.beginner.maxSteps) {
    return DIFFICULTY_LEVELS.beginner
  }
  if (stepCount <= DIFFICULTY_LEVELS.intermediate.maxSteps) {
    return DIFFICULTY_LEVELS.intermediate
  }
  return DIFFICULTY_LEVELS.advanced
}

export const getDifficultyInfo = (key) => {
  return DIFFICULTY_LEVELS[key] || null
}

export const getDifficultyStepRange = (key) => {
  const idx = DIFFICULTY_ORDER.indexOf(key)
  if (idx === -1) return ''
  const level = DIFFICULTY_LEVELS[key]
  if (level.maxSteps === Infinity) {
    const prevLevel = DIFFICULTY_LEVELS[DIFFICULTY_ORDER[idx - 1]]
    return `${prevLevel.maxSteps + 1}步以上`
  }
  if (idx === 0) {
    return `1-${level.maxSteps}步`
  }
  const prevLevel = DIFFICULTY_LEVELS[DIFFICULTY_ORDER[idx - 1]]
  return `${prevLevel.maxSteps + 1}-${level.maxSteps}步`
}

export const getDifficultyClass = (key) => {
  return key ? `difficulty-badge--${key}` : ''
}

export const CRAFT_NAME_TO_KEY = {}
for (const [key, val] of Object.entries(CRAFT_STYLES)) {
  CRAFT_NAME_TO_KEY[val.name] = key
}
CRAFT_NAME_TO_KEY['缝线'] = 'sewing'

export const getStepTypeInfo = (type) => CRAFT_STYLES[type] || CRAFT_STYLES.other

export const getCraftInfoByName = (name) => {
  const key = CRAFT_NAME_TO_KEY[name]
  if (key && CRAFT_STYLES[key]) return { ...CRAFT_STYLES[key], key }
  return { ...CRAFT_STYLES.other, key: 'other', name: name || '其他' }
}

export const getCraftClass = (name) => {
  const key = CRAFT_NAME_TO_KEY[name]
  return key && CRAFT_STYLES[key] ? `craft-badge--${key}` : 'craft-badge--other'
}

export const getStepClass = (type) => {
  return CRAFT_STYLES[type] ? `craft-badge--${type}` : 'craft-badge--other'
}

export const WORK_STATUS = {
  practice: {
    key: 'practice',
    name: '练习样件',
    icon: '📝',
    tagType: 'default',
    color: '#8c8c8c',
    bgColor: '#fafafa',
    borderColor: '#d9d9d9',
    description: '用于练习技艺的样件作品，不追求完美成品效果'
  },
  finished: {
    key: 'finished',
    name: '正式成品',
    icon: '✨',
    tagType: 'success',
    color: '#52c41a',
    bgColor: '#f6ffed',
    borderColor: '#b7eb8f',
    description: '精心制作完成的正式作品，用于展示或使用'
  },
  repair: {
    key: 'repair',
    name: '修复件',
    icon: '🔧',
    tagType: 'warning',
    color: '#fa8c16',
    bgColor: '#fff7e6',
    borderColor: '#ffd591',
    description: '对旧皮具进行修复、翻新的作品，记录修复过程'
  },
  semi_finished: {
    key: 'semi_finished',
    name: '半成品',
    icon: '⏳',
    tagType: 'info',
    color: '#1890ff',
    bgColor: '#e6f7ff',
    borderColor: '#91d5ff',
    description: '尚未完成的作品，记录制作进度，后续继续完善'
  }
}

export const WORK_STATUS_ORDER = ['finished', 'practice', 'semi_finished', 'repair']

export const getWorkStatusInfo = (key) => {
  return WORK_STATUS[key] || null
}

export const getWorkStatusClass = (key) => {
  return key ? `work-status-badge--${key}` : ''
}

export const getWorkStatusName = (key) => {
  const info = getWorkStatusInfo(key)
  return info ? info.name : ''
}

export const COMPLETION_LEVELS = {
  1: {
    key: 1,
    name: '粗坯',
    shortName: '粗坯',
    percent: 20,
    icon: '🪵',
    tagType: 'default',
    color: '#8c8c8c',
    bgColor: '#fafafa',
    borderColor: '#d9d9d9',
    description: '已完成皮料裁切与基础下料，尚未进行结构组装',
    hint: '适合记录制作初始阶段的裁切、粗加工状态'
  },
  2: {
    key: 2,
    name: '雏形',
    shortName: '雏形',
    percent: 40,
    icon: '📐',
    tagType: 'info',
    color: '#1890ff',
    bgColor: '#e6f7ff',
    borderColor: '#91d5ff',
    description: '主要部件已粗缝或粘合，作品整体轮廓已呈现',
    hint: '适合记录作品初成型、部件定位组合的阶段'
  },
  3: {
    key: 3,
    name: '半成',
    shortName: '半成',
    percent: 60,
    icon: '🧵',
    tagType: 'warning',
    color: '#fa8c16',
    bgColor: '#fff7e6',
    borderColor: '#ffd591',
    description: '主体缝制完成，结构已固定，待精细处理',
    hint: '适合记录主体完工、等待封边精修阶段的半成品'
  },
  4: {
    key: 4,
    name: '精修',
    shortName: '精修',
    percent: 80,
    icon: '✨',
    tagType: 'success',
    color: '#52c41a',
    bgColor: '#f6ffed',
    borderColor: '#b7eb8f',
    description: '封边、打磨、五金等关键工序完成，处于最终打磨优化阶段',
    hint: '适合记录作品即将完成、做最终收尾和修饰的阶段'
  },
  5: {
    key: 5,
    name: '完工',
    shortName: '完工',
    percent: 100,
    icon: '🏆',
    tagType: 'success',
    color: '#237804',
    bgColor: '#d9f7be',
    borderColor: '#73d13d',
    description: '所有工序全部完成，作品达到预期成品效果可交付使用',
    hint: '适合记录达到镜面封边、线迹工整、细节完美的最终成品'
  }
}

export const COMPLETION_ORDER = [1, 2, 3, 4, 5]

export const getCompletionInfo = (level) => {
  if (!level) return null
  const l = typeof level === 'string' ? parseInt(level, 10) : level
  return COMPLETION_LEVELS[l] || null
}

export const getCompletionName = (level) => {
  const info = getCompletionInfo(level)
  return info ? info.name : ''
}

export const getCompletionPercent = (level) => {
  const info = getCompletionInfo(level)
  return info ? info.percent : 0
}

export const getCompletionClass = (level) => {
  const info = getCompletionInfo(level)
  return info ? `completion-badge--L${info.key}` : ''
}

export const suggestCompletionByWorkStatus = (workStatusKey) => {
  const mapping = {
    finished: 5,
    repair: 5,
    practice: 4,
    semi_finished: 3
  }
  const level = mapping[workStatusKey]
  return level ? getCompletionInfo(level) : null
}

export const CATEGORY_CRAFT_HINTS = {
  passport_holder: {
    name: '护照夹',
    commonCrafts: ['sewing', 'edge', 'cutting', 'hardware'],
    excludedCrafts: ['carving', 'weaving'],
    hint: '护照夹结构紧凑、面积有限，常见缝制与封边工艺，不适合大面积皮雕或编织'
  },
  wallet: {
    name: '钱包/短夹',
    commonCrafts: ['sewing', 'edge', 'cutting', 'hardware', 'shaping'],
    optionalCrafts: ['carving', 'weaving'],
    hint: '钱包有足够平面可做皮雕装饰，小面积编织点缀也可行'
  },
  card_holder: {
    name: '卡包',
    commonCrafts: ['sewing', 'edge', 'cutting', 'hardware'],
    excludedCrafts: ['carving'],
    hint: '卡包空间紧凑、皮面有限，皮雕施展不开，建议聚焦基础缝制与封边'
  },
  belt: {
    name: '皮带',
    commonCrafts: ['cutting', 'edge', 'hardware'],
    excludedCrafts: ['carving', 'weaving'],
    hint: '皮带主要是直线条造型，重点在封边工艺，不适合皮雕或编织'
  },
  notebook: {
    name: '笔记本封皮',
    commonCrafts: ['cutting', 'edge', 'shaping', 'carving'],
    optionalCrafts: ['sewing', 'hardware'],
    excludedCrafts: ['weaving'],
    hint: '笔记本封皮是皮雕工艺的经典适用品类，平面充足且装饰性强'
  },
  bags: {
    name: '包袋类（手拿/单肩/托特）',
    commonCrafts: ['sewing', 'edge', 'cutting', 'hardware', 'shaping'],
    optionalCrafts: ['carving', 'weaving'],
    hint: '包袋类结构丰富、皮面充足，可自由搭配皮雕、编织等装饰工艺'
  }
}
