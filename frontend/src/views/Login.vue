<template>
  <div class="login-page">
    <van-nav-bar title="登录" left-arrow @click-left="$router.back()" fixed />

    <div class="login-content">
      <div class="logo-area">
      <div class="logo">🧶</div>
      <h2>手工皮具创作分享平台</h2>
      <p>分享你的创作，交流工艺</p>
    </div>

    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
          v-model="form.username"
          name="username"
          label="用户名"
          placeholder="请输入用户名"
          :rules="[{ required: true, message: '请填写用户名' }]"
        />
        <van-field
          v-model="form.password"
          type="password"
          name="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请填写密码' }]"
        />
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          登录
        </van-button>
      </div>
      <div class="register-link">
        还没有账号？<span @click="isRegister = true">立即注册</span>
      </div>
    </van-form>

    <van-dialog v-model:show="isRegister" title="注册" show-cancel-button :before-close="beforeRegisterClose">
      <van-form ref="registerFormRef">
        <van-cell-group inset>
          <van-field
            v-model="registerForm.username"
            label="用户名"
            placeholder="请输入用户名"
            :rules="[{ required: true, message: '请填写用户名' }]"
          />
          <van-field
            v-model="registerForm.password"
            type="password"
            label="密码"
            placeholder="请输入密码"
            :rules="[{ required: true, message: '请填写密码' }]"
          />
          <van-field
            v-model="registerForm.nickname"
            label="昵称"
            placeholder="请输入昵称（选填）"
          />
        </van-cell-group>
      </van-form>
    </van-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { login, register } from '@/api'
import { useUserStore } from '@/store/user'
import { showToast } from 'vant'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const form = ref({
  username: '',
  password: ''
})

const isRegister = ref(false)
const registerFormRef = ref(null)
const registerForm = ref({
  username: '',
  password: '',
  nickname: ''
})

const onSubmit = async () => {
  try {
    const user = await login(form.value)
    userStore.setUser(user)
    showToast('登录成功')
    setTimeout(() => {
      const redirect = route.query.redirect
      if (redirect && typeof redirect === 'string') {
        router.replace(redirect)
      } else {
        router.replace('/')
      }
    }, 1000)
  } catch (e) {
    // error handled by interceptor
  }
}

const beforeRegisterClose = async (action, done) => {
  if (action !== 'confirm') {
    done()
    return
  }
  try {
    await registerFormRef.value?.validate()
    await register(registerForm.value)
    showToast('注册成功，请登录')
    registerForm.value = { username: '', password: '', nickname: '' }
    done()
  } catch {
    done(false)
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: #fff;
}

.login-content {
  padding: 60px 16px 16px;
}

.logo-area {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  font-size: 60px;
  margin-bottom: 16px;
}

.logo-area h2 {
  font-size: 22px;
  margin: 0 0 8px;
}

.logo-area p {
  color: #999;
  margin: 0;
}

.register-link {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.register-link span {
  color: #1989fa;
}
</style>
