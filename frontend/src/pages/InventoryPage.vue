<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <el-icon class="page-icon"><Box /></el-icon>
        <h2 class="page-title">库存管理</h2>
      </div>
      <el-button type="primary" :icon="Plus" @click="handleAdd" size="large">
        新增库存
      </el-button>
    </div>

    <!-- 搜索卡片 -->
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="仓库">
          <el-select
            v-model="searchForm.warehouseId"
            placeholder="请选择仓库"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="item in warehouseList"
              :key="item.warehouseId"
              :label="item.warehouseName"
              :value="item.warehouseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="产品名称">
          <el-input
            v-model="searchForm.productName"
            placeholder="请输入产品名称"
            clearable
            :prefix-icon="Search"
            style="width: 200px"
          />
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
        <el-table-column prop="inventoryId" label="ID" width="80" align="center" />
        <el-table-column prop="warehouseName" label="仓库名称" width="150" />
        <el-table-column prop="productName" label="产品名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="color" label="颜色" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.color" size="small" effect="plain">{{ scope.row.color }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="size" label="尺码" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.size" size="small" effect="plain">{{ scope.row.size }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="总数量" width="110" align="right">
          <template #default="scope">
            <span class="quantity-text">{{ scope.row.quantity || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="allocatedQty" label="已分配" width="110" align="right">
          <template #default="scope">
            <span class="allocated-text">{{ scope.row.allocatedQty || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="availableQty" label="可用数量" width="120" align="right">
          <template #default="scope">
            <span class="available-text">{{ scope.row.availableQty || 0 }}</span>
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
        <el-form-item label="仓库" prop="warehouseId">
          <el-select v-model="form.warehouseId" placeholder="请选择仓库" style="width: 100%">
            <el-option
              v-for="item in warehouseList"
              :key="item.warehouseId"
              :label="item.warehouseName"
              :value="item.warehouseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="SKU ID" prop="skuId">
          <el-input-number v-model="form.skuId" :min="1" style="width: 100%" placeholder="请输入SKU ID" />
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number
            v-model="form.quantity"
            :min="0"
            style="width: 100%"
            placeholder="请输入数量"
          />
        </el-form-item>
        <el-form-item label="已分配数量" prop="allocatedQty">
          <el-input-number
            v-model="form.allocatedQty"
            :min="0"
            style="width: 100%"
            placeholder="请输入已分配数量"
          />
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
import { Plus, Search, Refresh, Edit, Delete, Box } from '@element-plus/icons-vue'
import { api } from '../main.js'

const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增库存')
const formRef = ref(null)
const warehouseList = ref([])

const form = reactive({
  inventoryId: null,
  warehouseId: null,
  skuId: null,
  quantity: 0,
  allocatedQty: 0
})

const searchForm = reactive({
  warehouseId: null,
  productName: ''
})

const rules = {
  warehouseId: [{ required: true, message: '仓库不能为空', trigger: 'change' }],
  skuId: [{ required: true, message: 'SKU不能为空', trigger: 'change' }],
  quantity: [{ required: true, message: '数量不能为空', trigger: 'blur' }]
}

const loadWarehouses = () => {
  api.get('/warehouses', { params: { pageNum: 1, pageSize: 1000 } }).then(res => {
    if (res.data && res.data.list) {
      warehouseList.value = res.data.list
    }
  }).catch(err => {
    console.error('获取仓库列表失败:', err)
  })
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
    .get('/inventory', { params })
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
    warehouseId: null,
    productName: ''
  })
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增库存'
  Object.assign(form, {
    inventoryId: null,
    warehouseId: null,
    skuId: null,
    quantity: 0,
    allocatedQty: 0
  })
  dialogVisible.value = true
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const handleEdit = row => {
  dialogTitle.value = '编辑库存'
  Object.assign(form, {
    inventoryId: row.inventoryId,
    warehouseId: row.warehouseId || null,
    skuId: row.skuId || null,
    quantity: row.quantity || 0,
    allocatedQty: row.allocatedQty || 0
  })
  dialogVisible.value = true
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const handleDelete = row => {
  ElMessageBox.confirm('确定要删除该库存记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      api.delete(`/inventory/${row.inventoryId}`).then(() => {
        ElMessage.success('删除成功')
        loadData()
      })
    })
    .catch(() => {})
}

const handleSave = () => {
  formRef.value.validate(valid => {
    if (valid) {
      const url = form.inventoryId ? `/inventory/${form.inventoryId}` : '/inventory'
      const method = form.inventoryId ? 'put' : 'post'
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
  loadWarehouses()
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

.quantity-text {
  font-weight: 600;
  color: #1e293b;
}

.allocated-text {
  font-weight: 500;
  color: #f59e0b;
}

.available-text {
  font-weight: 600;
  color: #10b981;
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
