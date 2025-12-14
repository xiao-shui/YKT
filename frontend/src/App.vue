<template>
  <el-container class="app-container">
    <!-- 顶部导航栏 -->
    <el-header class="app-header">
      <div class="header-content">
        <div class="header-left">
          <el-icon class="logo-icon"><Box /></el-icon>
          <h1 class="app-title">衣库通</h1>
          <span class="app-subtitle">库存管理系统</span>
        </div>
        <div class="header-right">
          <el-icon class="header-icon"><User /></el-icon>
          <span class="user-name">管理员</span>
        </div>
      </div>
    </el-header>

    <el-container>
      <!-- 侧边栏 -->
      <el-aside class="app-sidebar" width="240px">
        <el-menu
          :default-active="$route.path"
          router
          class="sidebar-menu"
          background-color="#1e293b"
          text-color="#cbd5e1"
          active-text-color="#3b82f6"
        >
          <el-menu-item index="/products" class="menu-item">
            <el-icon><Goods /></el-icon>
            <span class="menu-text">产品管理</span>
          </el-menu-item>
          <el-menu-item index="/warehouses" class="menu-item">
            <el-icon><OfficeBuilding /></el-icon>
            <span class="menu-text">仓库管理</span>
          </el-menu-item>
          <el-menu-item index="/inventory" class="menu-item">
            <el-icon><Box /></el-icon>
            <span class="menu-text">库存管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-main class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { Box, User, Goods, OfficeBuilding } from '@element-plus/icons-vue'
</script>

<style scoped>
.app-container {
  height: 100vh;
  overflow: hidden;
}

.app-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  padding: 0;
  height: 64px !important;
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 28px;
  color: white;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.app-title {
  font-size: 24px;
  font-weight: 600;
  color: white;
  margin: 0;
  letter-spacing: 2px;
}

.app-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin-left: 8px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
  color: white;
}

.header-icon {
  font-size: 20px;
}

.user-name {
  font-size: 14px;
}

.app-sidebar {
  background-color: #1e293b;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.sidebar-menu {
  border: none;
  height: 100%;
}

.menu-item {
  margin: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s;
}

.menu-item:hover {
  background-color: rgba(59, 130, 246, 0.1) !important;
}

.menu-item.is-active {
  background-color: rgba(59, 130, 246, 0.2) !important;
  color: #3b82f6 !important;
}

.menu-text {
  margin-left: 8px;
  font-weight: 500;
}

.app-main {
  background: linear-gradient(to bottom, #f8fafc, #f1f5f9);
  padding: 24px;
  overflow-y: auto;
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  width: 100%;
  height: 100vh;
}
</style>
