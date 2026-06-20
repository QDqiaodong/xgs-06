import request from '@/utils/request'

export const login = (data) => request.post('/user/login', data)
export const register = (data) => request.post('/user/register', data)
export const getUserInfo = (id) => request.get(`/user/${id}`)
export const updateUser = (data) => request.put('/user', data)
export const getCraftProfile = (userId) => request.get(`/user/craft-profile/${userId}`)

export const getCategories = (type) => request.get('/category/list', { params: { type } })
export const getCategory = (id) => request.get(`/category/${id}`)
export const getCraftTypesByCategory = (categoryId) => request.get('/category/craft-by-category', { params: { categoryId } })
export const validateCategoryCraft = (categoryId, craftTypeId) => request.get('/category/validate-craft', { params: { categoryId, craftTypeId } })

export const getWorkPage = (params) => request.get('/work/page', { params })
export const getWorkDetail = (id, config = {}) => request.get(`/work/${id}`, config)
export const publishWork = (data) => request.post('/work', data)
export const updateWork = (data) => request.put('/work', data)
export const offlineWork = (id) => request.delete(`/work/${id}`)
export const onlineWork = (id) => request.put(`/work/${id}/online`)
export const deleteWork = (id) => request.delete(`/work/${id}/permanent`)
export const getMyWorks = (params) => request.get('/work/my', { params })
export const getFavoriteWorks = (params) => request.get('/work/favorite', { params })
export const getHotWorks = () => request.get('/work/hot')
export const validateMaterials = (data) => request.post('/work/validate-materials', data)
export const getMaterialReference = () => request.get('/work/material-reference')

export const toggleFavorite = (workId) => request.post('/favorite/toggle', null, { params: { workId } })
export const checkFavorite = (workId) => request.get('/favorite/check', { params: { workId } })

export const getRetrospective = (workId) => request.get(`/retrospective/work/${workId}`)
export const getMyRetrospectives = (params) => request.get('/retrospective/my', { params })
export const saveRetrospective = (data) => request.post('/retrospective', data)
export const deleteRetrospective = (workId) => request.delete(`/retrospective/work/${workId}`)
