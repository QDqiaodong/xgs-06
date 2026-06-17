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
