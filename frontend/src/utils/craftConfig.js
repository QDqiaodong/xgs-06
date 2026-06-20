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
