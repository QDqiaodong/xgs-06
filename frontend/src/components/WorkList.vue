<template>
  <div class="work-list">
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
    <van-list
      v-model:loading="loading"
      :finished="finished"
      finished-text="没有更多了"
      @load="onLoad"
    >
      <div class="work-grid">
        <WorkCard v-for="work in list" :key="work.id" :work="work" />
      </div>
    </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getWorkPage } from '@/api'
import WorkCard from './WorkCard.vue'
import { useUserStore } from '@/store/user'

const props = defineProps({
  categoryId: {
    type: [Number, null],
    default: null
  },
  craftTypeId: {
    type: [Number, null],
    default: null
  },
  userId: {
    type: [Number, null],
    default: null
  },
  type: {
    type: String,
    default: 'all'
  }
})

const userStore = useUserStore()
const list = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const page = ref(1)
const pageSize = 10

const fetchList = async () => {
  try {
    let data
    if (props.type === 'my') {
      const { getMyWorks } = await import('@/api')
      data = await getMyWorks({
        page: page.value,
        size: pageSize,
        userId: props.userId
      })
    } else if (props.type === 'favorite') {
      const { getFavoriteWorks } = await import('@/api')
      data = await getFavoriteWorks({
        page: page.value,
        size: pageSize,
        userId: props.userId
      })
    } else {
      data = await getWorkPage({
        page: page.value,
        size: pageSize,
        categoryId: props.categoryId,
        craftTypeId: props.craftTypeId,
        userId: userStore.userInfo?.id
      })
    }

    if (refreshing.value) {
      list.value = data.records
      refreshing.value = false
    } else {
      list.value = [...list.value, ...data.records]
    }

    loading.value = false

    if (list.value.length >= data.total) {
      finished.value = true
    }
  } catch (e) {
    loading.value = false
    refreshing.value = false
  }
}

const onLoad = () => {
  page.value++
  fetchList()
}

const onRefresh = () => {
  finished.value = false
  page.value = 1
  fetchList()
}

fetchList()
</script>

<style scoped>
.work-list {
  padding: 12px;
}

.work-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}
</style>
