<!-- UserProfile.vue -->
<script>
export default {
  data() {
    return {
      userInfo: {
        id: '',
        name: '',
        phone: ''
      },
      centerDialogVisible: false,
      form: {
        id: '',
        name: '',
        phone: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入电话号码', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ]
      },
      courseCount: 3,
      notificationCount: 5,
      favoriteCount: 2,
      // 分页参数
      pageSize: 10,
      pageNum: 1
    }
  },
  methods: {
    // 从sessionStorage获取当前用户信息
    getCurrentUser() {
      const userStr = sessionStorage.getItem("CurUser")
      if (userStr) {
        const userData = JSON.parse(userStr)
        return userData.data || userData // 如果有data属性就返回data，否则返回整个对象
      }
      return null
    },
    resetForm() {
      this.$refs.form.resetFields()
    },

    handleEdit() {
      this.centerDialogVisible = true
      this.$nextTick(() => {
        this.form.id = this.userInfo.id
        this.form.name = this.userInfo.name
        this.form.phone = this.userInfo.phone
      })
    },

    save() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.doUpdate()
        } else {
          return false
        }
      })
    },

    async doUpdate() {
      try {
        const res = await this.$axios.post(this.$httpUrl + '/user/update', this.form)
        if (res.data.code == 200) {
          this.$message({
            message: '更新成功！',
            type: 'success'
          })

          // 更新成功后，更新sessionStorage中的用户信息
          const curUser = this.getCurrentUser()
          if (curUser) {
            curUser.name = this.form.name
            curUser.phone = this.form.phone
            sessionStorage.setItem("CurUser", JSON.stringify(curUser))
          }

          this.centerDialogVisible = false
          this.loadUserInfo()
          this.resetForm()
        } else {
          this.$message.error(res.data.message || '更新失败')
        }
      } catch (error) {
        console.error('更新失败：', error)
        this.$message.error('更新失败，请检查网络连接')
      }
    },

    async loadUserInfo() {
      try {
        // 先从sessionStorage获取用户信息
        const curUser = this.getCurrentUser()
        if (!curUser || !curUser.id) {
          this.$message.error('未获取到用户信息，请重新登录')
          return
        }

        // 使用用户ID查询最新信息
        const res = await this.$axios.post(this.$httpUrl + '/user/listPageC1', {
          pageSize: this.pageSize,
          pageNum: this.pageNum,
          param: {
            id: curUser.id
          }
        })

        if (res.data.code == 200 && res.data.data.length > 0) {
          this.userInfo = res.data.data[0]

          // 更新sessionStorage中的用户信息
          curUser.name = this.userInfo.name
          curUser.phone = this.userInfo.phone
          sessionStorage.setItem("CurUser", JSON.stringify(curUser))
        } else {
          // 如果接口查询失败，直接使用sessionStorage中的信息
          this.userInfo = curUser
        }
      } catch (error) {
        console.error('获取用户信息失败：', error)
        // 如果接口调用失败，使用sessionStorage中的信息
        const curUser = this.getCurrentUser()
        if (curUser) {
          this.userInfo = curUser
        } else {
          this.$message.error('获取用户信息失败，请重新登录')
        }
      }
    },

    // 页面跳转方法
    goToCourses() {
      // 从 userInfo 中获取角色信息
      const roleId = this.userInfo.roleId;

      // 根据角色 ID 跳转到不同页面
      if (roleId === 0) { // 学生
        this.$router.push('/SCourse');
      } else if (roleId === 1) { // 教师
        this.$router.push('/TCourse');
      } else {
        this.$message.error('未知的用户角色');
      }
    },

    goToNotifications() {
      this.$router.push('/SNotification')
    },

    goToFavorites() {
      this.$router.push('/Bookmark')
    }
  },

  created() {
    // 组件创建时，先尝试从sessionStorage加载用户信息
    const curUser = this.getCurrentUser()
    if (curUser) {
      this.userInfo = curUser
    }
    // 然后从后端获取最新信息
    this.loadUserInfo()
  }
}
</script>

<template>
  <!-- 其他模板内容保持不变 -->
  <div style="height: 100vh">
    <!-- 功能按钮区域 -->
    <div style="margin-bottom: 20px;">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card shadow="hover" class="function-card" @click.native="goToCourses">
            <div class="card-content">
              <i class="el-icon-reading icon"></i>
              <div class="title">我的课程</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="function-card" @click.native="goToNotifications">
            <div class="card-content">
              <i class="el-icon-bell icon"></i>
              <div class="title">通知提醒</div>

            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="function-card" @click.native="goToFavorites">
            <div class="card-content">
              <i class="el-icon-star-on icon"></i>
              <div class="title">我的收藏夹</div>

            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 个人信息展示区域 -->
    <div style="margin-bottom: 20px;">
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <span>个人信息</span>
          <el-button style="float: right; padding: 3px 0" type="text" @click="handleEdit">编辑信息</el-button>
        </div>
        <div class="info-content">
          <div class="info-item">
            <span class="label">账号：</span>
            <span>{{ userInfo.id }}</span>
          </div>
          <div class="info-item">
            <span class="label">姓名：</span>
            <span>{{ userInfo.name }}</span>
          </div>
          <div class="info-item">
            <span class="label">电话：</span>
            <span>{{ userInfo.phone }}</span>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog
        title="编辑个人信息"
        :visible.sync="centerDialogVisible"
        width="30%"
        center>
      <el-form ref="form" :rules="rules" :model="form" label-width="80px">
        <el-form-item label="账号">
          <el-col :span="20">
            <el-input v-model="form.id" disabled></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-col :span="20">
            <el-input v-model="form.name"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-col :span="20">
            <el-input v-model="form.phone"></el-input>
          </el-col>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-container {
  min-height: calc(100vh - 60px); /* 假设顶部导航栏高度为60px */
  padding: 20px;
  box-sizing: border-box;
  background-color: #f5f7fa;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.cards-section {
  margin-bottom: 20px;
}

.info-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.box-card {
  height: 100%;
}

.function-card {
  cursor: pointer;
  transition: all 0.3s;
  height: 100%;
}

.function-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}

.card-content {
  text-align: center;
  padding: 20px;
}

.icon {
  font-size: 40px;
  color: #409EFF;
  margin-bottom: 10px;
}

.title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.count {
  font-size: 14px;
  color: #909399;
}

.info-content {
  padding: 20px;
  flex: 1;
}

.info-item {
  margin-bottom: 20px;
  line-height: 24px;
  padding: 10px;
  background-color: #fafafa;
  border-radius: 4px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.label {
  display: inline-block;
  width: 70px;
  color: #606266;
  font-weight: 500;
}

.badge {
  margin-top: -5px;
}

@media screen and (max-width: 768px) {
  .page-container {
    padding: 10px;
  }

  .cards-section {
    margin-bottom: 10px;
  }

  .info-content {
    padding: 10px;
  }
}
</style>