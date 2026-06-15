import request from '@/utils/request'

export const login = (data) => request.post('/user/login', data)
export const register = (data) => request.post('/user/register', data)
export const getUserInfo = (id) => request.get(`/user/${id}`)
export const updateUser = (data) => request.put('/user', data)

export const getCategories = (type) => request.get('/category/list', { params: { type } })

export const getWorkPage = (params) => request.get('/work/page', { params })
export const getWorkDetail = (id, userId, config = {}) => request.get(`/work/${id}`, {
  ...config,
  params: { userId, ...(config.params || {}) }
})
export const publishWork = (data, userId) => request.post('/work', data, { params: { userId } })
export const updateWork = (data, userId) => request.put('/work', data, { params: { userId } })
export const offlineWork = (id, userId) => request.delete(`/work/${id}`, { params: { userId } })
export const deleteWork = (id, userId) => request.delete(`/work/${id}/permanent`, { params: { userId } })
export const getMyWorks = (params) => request.get('/work/my', { params })
export const getFavoriteWorks = (params) => request.get('/work/favorite', { params })
export const getHotWorks = () => request.get('/work/hot')

export const toggleFavorite = (userId, workId) => request.post('/favorite/toggle', null, { params: { userId, workId } })
export const checkFavorite = (userId, workId) => request.get('/favorite/check', { params: { userId, workId } })
