<template>
  <van-tabbar v-model="active" route>
    <van-tabbar-item to="/" icon="wap-home">首页</van-tabbar-item>
    <van-tabbar-item to="/publish" icon="plus">发布</van-tabbar-item>
    <van-tabbar-item to="/profile" icon="user">我的</van-tabbar-item>
  </van-tabbar>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const active = ref(route.path)
</script>
