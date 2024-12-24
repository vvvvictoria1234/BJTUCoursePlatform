<template>
  <div>
    <!-- 课程列表部分 -->
    <div class="course-list">
      <h2>我的课程</h2>
      <el-table
          :data="courseList"
          :header-cell-style="{ background: '#f2f5fc', color: '#555555' }"
          border>
        <el-table-column prop="courseId" label="课程编号" width="120"></el-table-column>
        <el-table-column prop="courseName" label="课程名称" width="200"></el-table-column>
        <el-table-column prop="teacherId" label="授课教师" width="120"></el-table-column>
        <el-table-column prop="courseDescription" label="课程描述" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="280">
          <template slot-scope="scope">
            <el-button size="small" type="primary" @click="viewCourseDetail(scope.row.courseId)">
              课程详情
            </el-button>
            <el-button size="small" type="success" @click="viewCourseResources(scope.row.courseId)">
              课程资源
            </el-button>
            <el-button size="small" type="info" @click="viewNotices(scope.row.courseId)">
              课程通知
            </el-button>
          </template>
        </el-table-column>
      </el-table>

    </div>

    <!-- 课程详情对话框 -->
    <el-dialog
        title="课程详情"
        :visible.sync="courseDetailVisible"
        width="60%">
      <div v-if="currentCourse" class="course-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="课程名称">{{ currentCourse.courseName }}</el-descriptions-item>
          <el-descriptions-item label="课程编号">{{ currentCourse.courseId }}</el-descriptions-item>
          <el-descriptions-item label="授课教师">{{ currentCourse.teacher_name }}</el-descriptions-item>
          <el-descriptions-item label="教师ID">{{ currentCourse.teacherId }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-section">
          <h3>课程介绍</h3>
          <p>{{ currentCourse.courseDescription }}</p>
        </div>

        <div class="detail-section">
          <h3>教学大纲</h3>
          <div v-html="currentCourse.syllabus"></div>
        </div>

        <div class="detail-section">
          <h3>教学日历</h3>
          <div v-html="currentCourse.calendar"></div>
        </div>
      </div>
    </el-dialog>

    <!-- 课程资源对话框 -->
    <el-dialog
        title="课程资源"
        :visible.sync="resourcesVisible"
        width="70%">
      <el-table :data="courseResources" border>
        <el-table-column prop="resourceId" label="资源编号"></el-table-column>
        <el-table-column prop="fileName" label="文件名称"></el-table-column>
        <el-table-column prop="fileType" label="文件类型" width="120"></el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button
                type="primary"
                size="small"
                @click="downloadResource(scope.row.resourceId)">
              下载
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 课程通知对话框 -->
    <el-dialog
        title="课程通知"
        :visible.sync="noticesVisible"
        width="60%">
      <el-timeline>
        <el-timeline-item
            v-for="notice in courseNotices"
            :key="notice.notice_id"
            :timestamp="notice.publish_time"
            placement="top">
          <el-card>
            <h4>{{ notice.title }}</h4>
            <p>{{ notice.content }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'StudentCourseManagement',
  data() {
    return {
      // 课程列表数据
      courseList: [],
      // 当前课程详情
      currentCourse: null,
      // 课程资源列表
      courseResources: [],
      // 课程通知列表
      courseNotices: [],
      // 对话框显示控制
      courseDetailVisible: false,
      resourcesVisible: false,
      noticesVisible: false,
      // 移除资源标签页相关状态
      // 用户信息
      userInfo: {
        id: '',
        name: '',
        phone: ''
      },
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
    this.loadCourseList()
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
    async loadUserInfo() {
      try {
        // 先从sessionStorage获取用户信息
        const curUser = this.getCurrentUser()
        console.log(curUser.id)
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
    // 加载学生课程列表
    async loadCourseList() {
      try {
        if (!this.userInfo || !this.userInfo.id) {
          this.$message.error('未获取到用户信息')
          return
        }

        const res = await this.$axios.get(this.$httpUrl + '/course/student/courses/' + this.userInfo.id)
        if (res.data.code === 200) {
          this.courseList = res.data.data
          console.log(this.courseList)
        } else {
          this.$message.error('获取课程列表失败')
        }
      } catch (error) {
        console.error('加载课程列表失败:', error)
        this.$message.error('获取课程列表失败')
      }
    },

    // 查看课程详情
    async viewCourseDetail(courseId) {
      try {
        const res = await this.$axios.get(this.$httpUrl + '/course/detail/' + courseId)
        if (res.data.code === 200) {
          this.currentCourse = res.data.data
          this.courseDetailVisible = true
        } else {
          this.$message.error('获取课程详情失败')
        }
      } catch (error) {
        console.error('获取课程详情失败:', error)
        this.$message.error('获取课程详情失败')
      }
    },

    // 查看课程资源
    async viewCourseResources(courseId) {
      try {
        const res = await this.$axios.get(this.$httpUrl + '/course/resources/' + courseId)
        if (res.data.code === 200) {
          this.courseResources = res.data.data
          console.log(this.courseResources)
          this.resourcesVisible = true
        } else {
          this.$message.error('获取课程资源失败')
        }
      } catch (error) {
        console.error('获取课程资源失败:', error)
        this.$message.error('获取课程资源失败')
      }
    },

    // 查看课程通知
    async viewNotices(courseId) {
      try {
        const res = await this.$axios.get(this.$httpUrl + '/course/notices/' + courseId)
        if (res.data.code === 200) {
          this.courseNotices = res.data.data
          this.noticesVisible = true
        } else {
          this.$message.error('获取课程通知失败')
        }
      } catch (error) {
        console.error('获取课程通知失败:', error)
        this.$message.error('获取课程通知失败')
      }
    },

    async downloadResource(resourceId) {
      try {
        // 使用axios直接下载文件
        const response = await this.$axios.get(
            this.$httpUrl + '/course/resource/download/' + resourceId,
            {
              responseType: 'blob' // 重要：设置响应类型为blob
            }
        );

        // 从响应头中获取文件名
        const contentDisposition = response.headers['content-disposition'];
        let fileName = 'download'; // 默认文件名

        if (contentDisposition) {
          const fileNameMatch = contentDisposition.match(/filename="(.+)"/);
          if (fileNameMatch.length === 2) {
            fileName = decodeURIComponent(fileNameMatch[1]);
          }
        }

        // 创建Blob对象并下载
        const blob = new Blob([response.data], {
          type: response.headers['content-type']
        });

        const link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = fileName;
        link.click();
        window.URL.revokeObjectURL(link.href);

        this.$message.success('下载成功');
      } catch (error) {
        console.error('下载文件失败:', error);
        this.$message.error('下载失败，请重试');
      }
    }
  }
}
</script>

<style scoped>
.course-list {
  padding: 20px;
}

.detail-section {
  margin-top: 20px;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.detail-section h3 {
  margin-bottom: 10px;
  color: #303133;
  font-size: 16px;
}

.detail-section p {
  margin: 0;
  line-height: 1.6;
  color: #606266;
}

.el-timeline {
  padding: 20px;
}
</style>