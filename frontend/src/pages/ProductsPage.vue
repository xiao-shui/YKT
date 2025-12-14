<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <el-icon class="page-icon"><Goods /></el-icon>
        <h2 class="page-title">产品管理</h2>
      </div>
      <el-button type="primary" :icon="Plus" @click="handleAdd" size="large">
        新增产品
      </el-button>
    </div>

    <!-- 搜索卡片 -->
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="产品名称">
          <el-input
            v-model="searchForm.productName"
            placeholder="请输入产品名称"
            clearable
            :prefix-icon="Search"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-input
            v-model="searchForm.category"
            placeholder="请输入分类"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="品牌">
          <el-input
            v-model="searchForm.brand"
            placeholder="请输入品牌"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="上架" value="上架" />
            <el-option label="下架" value="下架" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格卡片 -->
    <el-card class="table-card" shadow="hover">
      <el-table
        :data="tableData"
        style="width: 100%"
        border
        stripe
        v-loading="loading"
        :header-cell-style="{ background: '#f8fafc', color: '#1e293b', fontWeight: '600' }"
      >
        <el-table-column prop="productId" label="ID" width="80" align="center" />
        <el-table-column prop="productName" label="产品名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120" align="center" />
        <el-table-column prop="brand" label="品牌" width="120" align="center" />
        <el-table-column prop="basePrice" label="基础价格" width="130" align="right">
          <template #default="scope">
            <span class="price-text">¥{{ scope.row.basePrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === '上架' ? 'success' : 'info'" effect="dark" size="small">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" :icon="Edit" size="small" link @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" :icon="Delete" size="small" link @click="handleDelete(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="680px"
      :close-on-click-modal="false"
      class="form-dialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" label-position="left">
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入产品名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="form.category" placeholder="请输入分类" />
        </el-form-item>
        <el-form-item label="品牌" prop="brand">
          <el-input v-model="form.brand" placeholder="请输入品牌" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入产品描述"
          />
        </el-form-item>
        <el-form-item label="基础价格" prop="basePrice">
          <el-input-number
            v-model="form.basePrice"
            :min="0.01"
            :precision="2"
            style="width: 100%"
            placeholder="请输入基础价格"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="上架" value="上架" />
            <el-option label="下架" value="下架" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, Goods } from '@element-plus/icons-vue'
import { api } from '../main.js'

const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增产品')
const formRef = ref(null)

const form = reactive({
  productId: null,
  productName: '',
  category: '',
  brand: '',
  description: '',
  basePrice: null,
  status: '上架'
})

const searchForm = reactive({
  productName: '',
  category: '',
  brand: '',
  status: ''
})

const rules = {
  productName: [{ required: true, message: '产品名称不能为空', trigger: 'blur' }],
  basePrice: [{ required: true, message: '基础价格不能为空', trigger: 'blur' }]
}

const loadData = () => {
  loading.value = true
  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    ...searchForm
  }
  // 过滤掉空值
  Object.keys(params).forEach(key => {
    if (params[key] === '' || params[key] === null || params[key] === undefined) {
      delete params[key]
    }
  })
  api
    .get('/products', { params })
    .then(res => {
      if (res.data && res.data.list) {
        tableData.value = res.data.list
        total.value = res.data.total
      } else {
        console.error('响应数据结构异常:', res)
        ElMessage.error('获取数据失败，请检查响应格式')
      }
    })
    .catch(err => {
      console.error('请求失败:', err)
      ElMessage.error('获取数据失败')
    })
    .finally(() => {
      loading.value = false
    })
}

const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

const handleReset = () => {
  Object.assign(searchForm, {
    productName: '',
    category: '',
    brand: '',
    status: ''
  })
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增产品'
  Object.assign(form, {
    productId: null,
    productName: '',
    category: '',
    brand: '',
    description: '',
    basePrice: null,
    status: '上架'
  })
  dialogVisible.value = true
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const handleEdit = row => {
  dialogTitle.value = '编辑产品'
  Object.assign(form, {
    productId: row.productId,
    productName: row.productName || '',
    category: row.category || '',
    brand: row.brand || '',
    description: row.description || '',
    basePrice: row.basePrice || null,
    status: row.status || '上架'
  })
  dialogVisible.value = true
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const handleDelete = row => {
  ElMessageBox.confirm('确定要删除该产品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      api.delete(`/products/${row.productId}`).then(() => {
        ElMessage.success('删除成功')
        loadData()
      })
    })
    .catch(() => {})
}

const handleSave = () => {
  formRef.value.validate(valid => {
    if (valid) {
      const url = form.productId ? `/products/${form.productId}` : '/products'
      const method = form.productId ? 'put' : 'post'
      api[method](url, form).then(() => {
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadData()
      })
    }
  })
}

const handleSizeChange = val => {
  pageSize.value = val
  loadData()
}

const handleCurrentChange = val => {
  pageNum.value = val
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 20px 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-icon {
  font-size: 28px;
  color: #667eea;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.search-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.search-form {
  margin: 0;
}

.table-card {
  border-radius: 12px;
}

.price-text {
  color: #f56c6c;
  font-weight: 600;
  font-size: 15px;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

.form-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.form-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.form-dialog :deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid #e5e7eb;
}
</style>
