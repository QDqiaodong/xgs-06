<template>
  <div class="home-page">
    <van-nav-bar title="手工皮具创作" fixed />

    <div class="page-content">
      <div class="filter-bar">
        <van-tabs v-model:active="activeTab" scrollspy sticky offset-top="46">
          <van-tab title="全部">
            <WorkList :category-id="null" :craft-type-id="null" />
          </van-tab>
          <van-tab v-for="cat in categories" :key="cat.id" :title="cat.name">
            <WorkList :category-id="cat.id" :craft-type-id="null" />
          </van-tab>
        </van-tabs>
      </div>
    </div>

    <BottomNav />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCategories } from '@/api'
import WorkList from '@/components/WorkList.vue'
import BottomNav from '@/components/BottomNav.vue'

const activeTab = ref(0)
const categories = ref([])

onMounted(async () => {
  categories.value = await getCategories('category')
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  padding-bottom: 50px;
}

.page-content {
  padding-top: 46px;
}

.filter-bar {
  background: #fff;
}
</style>
