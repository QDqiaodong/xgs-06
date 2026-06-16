const VALID_LEATHER_TYPES = [
  '植鞣革', '植鞣', '铬鞣革', '铬鞣', '半植鞣', '油蜡皮', '油皮', '蜡皮',
  '疯马皮', '疯马', '磨砂皮', '反绒皮', '反绒', '纳帕皮', '纳帕', '粒面皮',
  '修面皮', '荔枝纹', '摔纹皮', '压花皮', '鳄鱼纹', '鸵鸟纹', '蛇纹',
  '羊皮', '山羊皮', '绵羊皮', '牛皮', '黄牛皮', '水牛皮', '马皮', '猪皮',
  '鹿皮', '鸵鸟皮', '鳄鱼皮', '蜥蜴皮', '蟒蛇皮', '鲨鱼皮',
  '头层皮', '二层皮', '三层皮', '再生皮', '复合皮', '人造革', 'PU', 'PVC'
]

const VALID_COLORS = [
  '原色', '自然色', '本色',
  '黑色', '白色', '红色', '黄色', '蓝色', '绿色', '紫色', '橙色', '粉色', '灰色', '棕色', '褐色',
  '深棕', '浅棕', '红棕', '黄棕', '咖啡', '咖啡色', '巧克力', '巧克力色',
  '深红', '酒红', '枣红', '砖红', '正红', '中国红',
  '深蓝', '藏蓝', '宝蓝', '天蓝', '湖蓝', '孔雀蓝',
  '深绿', '墨绿', '草绿', '军绿', '橄榄绿', '薄荷绿',
  '深黄', '浅黄', '米黄', '杏黄', '鹅黄', '姜黄', '柠檬黄',
  '深灰', '浅灰', '银灰', '炭灰',
  '米白', '乳白', '象牙白', '珍珠白', '雪白',
  '驼色', '米色', '卡其', '卡其色', '杏色', '藕色', '肉色',
  '金色', '银色', '古铜色', '青铜色', '玫瑰金'
]

const VALID_QUANTITY_UNITS = [
  '张', '片', '块', '码', '英尺', '平方英尺',
  '米', '厘米', '毫米',
  '克', '千克',
  '个', '套', '组', '卷', '条', '公尺'
]

const VAGUE_NAMES = [
  '皮料', '皮', '材料', '物料', '料子', '主材', '辅材', '工具',
  '皮子', '皮革', '皮张', '皮件', '配件', '五金', '线', '绳子'
]

const THICKNESS_REGEX = /^(\d+(?:\.\d{1,2})?)\s*(mm|毫米|厘米|cm)?$/i
const QUANTITY_REGEX = /^(\d+(?:\.\d{1,3})?)\s*([\u4e00-\u9fa5a-zA-Z]+)?$/

export const THICKNESS_OPTIONS = [
  { value: '0.5mm', label: '0.5mm（薄）' },
  { value: '0.8mm', label: '0.8mm' },
  { value: '1.0mm', label: '1.0mm' },
  { value: '1.2mm', label: '1.2mm' },
  { value: '1.5mm', label: '1.5mm（常用）' },
  { value: '1.8mm', label: '1.8mm（常用）' },
  { value: '2.0mm', label: '2.0mm（常用）' },
  { value: '2.5mm', label: '2.5mm' },
  { value: '3.0mm', label: '3.0mm（厚）' },
  { value: '3.5mm', label: '3.5mm（厚）' },
  { value: '4.0mm', label: '4.0mm（加厚）' }
]

export const LEATHER_TYPE_OPTIONS = [
  { value: '植鞣革', label: '植鞣革（适合雕刻塑形）' },
  { value: '铬鞣革', label: '铬鞣革（柔软耐用）' },
  { value: '半植鞣', label: '半植鞣（软硬适中）' },
  { value: '疯马皮', label: '疯马皮（复古油蜡感）' },
  { value: '油蜡皮', label: '油蜡皮（光泽油润）' },
  { value: '磨砂皮', label: '磨砂皮（绒面质感）' },
  { value: '反绒皮', label: '反绒皮（细腻柔软）' },
  { value: '纳帕皮', label: '纳帕皮（细腻粒面）' },
  { value: '摔纹皮', label: '摔纹皮（自然纹理）' },
  { value: '荔枝纹', label: '荔枝纹（经典纹理）' },
  { value: '羊皮', label: '羊皮（轻薄柔软）' },
  { value: '牛皮', label: '牛皮（结实耐用）' },
  { value: '头层皮', label: '头层皮（品质最佳）' },
  { value: '二层皮', label: '二层皮（经济实惠）' }
]

export const COLOR_OPTIONS = [
  { value: '原色', label: '原色（可养色）' },
  { value: '深棕', label: '深棕' },
  { value: '浅棕', label: '浅棕' },
  { value: '红棕', label: '红棕' },
  { value: '咖啡色', label: '咖啡色' },
  { value: '黑色', label: '黑色' },
  { value: '酒红', label: '酒红' },
  { value: '藏蓝', label: '藏蓝' },
  { value: '墨绿', label: '墨绿' },
  { value: '军绿', label: '军绿' },
  { value: '驼色', label: '驼色' },
  { value: '米白', label: '米白' },
  { value: '灰色', label: '灰色' },
  { value: '红色', label: '红色' },
  { value: '蓝色', label: '蓝色' }
]

export function isLikelyLeather(name) {
  if (!name) return false
  for (const type of VALID_LEATHER_TYPES) {
    if (name.includes(type)) {
      return true
    }
  }
  return name.includes('皮') || name.includes('革')
}

export function isVagueName(name) {
  if (!name) return false
  const trimmed = name.trim()
  if (VAGUE_NAMES.includes(trimmed)) return true
  if (trimmed.length <= 2 && trimmed.includes('皮')) {
    return !VALID_LEATHER_TYPES.includes(trimmed)
  }
  return false
}

export function validateSingleMaterial(material, prefix = '') {
  const errors = []
  const warnings = []

  if (!material) {
    errors.push(`${prefix}数据缺失`)
    return { errors, warnings, valid: errors.length === 0 }
  }

  const name = (material.name || '').trim()
  const spec = (material.spec || '').trim()
  const quantity = (material.quantity || '').trim()

  if (!name) {
    errors.push(`${prefix}名称不能为空`)
  } else if (name.length > 30) {
    errors.push(`${prefix}名称过长（最多30字）`)
  } else if (/^[\s!"#$%&'()*+,\-./:;<=>?@[\\\]^_`{|}~，。！？；：""''（）【】《》、…—]+$/.test(name)) {
    errors.push(`${prefix}名称不能仅包含符号或空格`)
  } else if (isVagueName(name)) {
    warnings.push(`${prefix}名称"${name}"过于空泛，建议使用具体名称如"植鞣革"、"疯马皮"等`)
  }

  if (spec.length > 50) {
    errors.push(`${prefix}规格过长（最多50字）`)
  }

  const isLeather = isLikelyLeather(name)
  if (isLeather && spec) {
    const leatherWarnings = validateLeatherSpec(spec, prefix)
    warnings.push(...leatherWarnings)
  }

  if (quantity) {
    if (quantity.length > 30) {
      errors.push(`${prefix}用量过长（最多30字）`)
    } else {
      const qtyResult = validateQuantity(quantity, prefix)
      warnings.push(...qtyResult.warnings)
      errors.push(...qtyResult.errors)
    }
  }

  return { errors, warnings, valid: errors.length === 0 }
}

export function validateLeatherSpec(spec, prefix = '') {
  const warnings = []
  let hasThickness = false
  let hasColor = false
  let hasValidLeatherType = false

  for (const type of VALID_LEATHER_TYPES) {
    if (spec.includes(type)) {
      hasValidLeatherType = true
      break
    }
  }

  const thicknessMatch = spec.match(THICKNESS_REGEX)
  if (thicknessMatch) {
    hasThickness = true
    try {
      let thickness = parseFloat(thicknessMatch[1])
      const unit = thicknessMatch[2]
      if (unit && (unit === '厘米' || unit.toLowerCase() === 'cm')) {
        thickness = thickness * 10
      }
      if (thickness < 0.3) {
        warnings.push(`${prefix}厚度${thickness}mm 偏小，常规皮料厚度在0.5mm-6mm之间`)
      } else if (thickness > 8.0) {
        warnings.push(`${prefix}厚度${thickness}mm 偏大，常规皮料厚度在0.5mm-6mm之间`)
      }
    } catch (e) {
      // ignore
    }
  }

  for (const color of VALID_COLORS) {
    if (spec.includes(color)) {
      hasColor = true
      break
    }
  }

  if (!hasThickness && !hasColor && !hasValidLeatherType) {
    warnings.push(`${prefix}皮料规格建议包含厚度（如2.0mm）、颜色（如深棕）或皮种信息，便于展示统计`)
  }

  return warnings
}

export function validateQuantity(quantity, prefix = '') {
  const errors = []
  const warnings = []

  const match = quantity.match(QUANTITY_REGEX)
  if (!match) {
    warnings.push(`${prefix}用量格式建议为"数字+单位"，如"1张"、"0.5米"`)
    return { errors, warnings }
  }

  const qtyValue = parseFloat(match[1])
  if (qtyValue <= 0) {
    errors.push(`${prefix}用量必须大于0`)
  }

  const unit = match[2]
  if (unit) {
    let validUnit = false
    for (const valid of VALID_QUANTITY_UNITS) {
      if (unit === valid || unit.toLowerCase() === valid.toLowerCase()) {
        validUnit = true
        break
      }
    }
    if (!validUnit) {
      warnings.push(`${prefix}单位"${unit}"不在常见单位列表中，建议使用：张、片、米、英尺、个、套等`)
    }
  }

  return { errors, warnings }
}

export function validateAllMaterials(materialSummaryObj, materialsText = '') {
  const allErrors = []
  const allWarnings = []
  const sections = [
    { key: 'mainMaterials', label: '主材' },
    { key: 'auxMaterials', label: '辅材' },
    { key: 'tools', label: '工具' }
  ]

  let totalCount = 0
  for (const section of sections) {
    const items = materialSummaryObj?.[section.key] || []
    totalCount += items.length
    for (let i = 0; i < items.length; i++) {
      const prefix = `${section.label}第${i + 1}项：`
      const result = validateSingleMaterial(items[i], prefix)
      allErrors.push(...result.errors)
      allWarnings.push(...result.warnings)
    }
  }

  if (totalCount === 0 && !materialsText?.trim()) {
    allWarnings.push('材料信息为空，建议填写主材信息以便统计展示')
  }

  return {
    errors: allErrors,
    warnings: allWarnings,
    valid: allErrors.length === 0
  }
}

export function getMaterialNameSuggestions(input) {
  if (!input) return []
  const lower = input.toLowerCase()
  return VALID_LEATHER_TYPES.filter(t => t.toLowerCase().includes(lower)).slice(0, 8)
}

export function getColorSuggestions(input) {
  if (!input) return []
  const lower = input.toLowerCase()
  return VALID_COLORS.filter(c => c.toLowerCase().includes(lower)).slice(0, 8)
}

export function getUnitSuggestions() {
  return VALID_QUANTITY_UNITS
}
