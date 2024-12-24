<template>
  <div class="notice-center">
    <!-- 页面标题和过滤器 -->
    <div class="filter-section">
      <h2>通知中心</h2>
      <el-select
          v-model="selectedCourse"
          placeholder="选择课程"
          clearable
          @change="filterNotices"
          style="width: 200px"
      >
        <el-option
            v-for="course in courseList"
            :key="course.courseId"
            :label="course.courseName"
            :value="course.courseId"
        />
      </el-select>
    </div>

    <!-- 通知列表 -->
    <div class="notice-list">
      <el-timeline>
        <el-timeline-item
            v-for="notice in filteredNotices"
            :key="notice.noticeId"
            :timestamp="formatTime(notice.publishTime)"
            :type="getNoticeType(notice)"
            size="large"
        >
          <el-card class="notice-card">
            <div class="notice-header">
              <h3>{{ notice.title }}</h3>
              <el-tag size="small">{{ notice.courseName }}</el-tag>
            </div>
            <div class="notice-content">{{ notice.content }}</div>

          </el-card>
        </el-timeline-item>
      </el-timeline>

      <!-- 无通知时显示的提示 -->
      <el-empty
          v-if="filteredNotices.length === 0"
          description="暂无通知"
      />
    </div>
  </div>
</template>

<script>
export default {
  name: 'NoticeCenter',
  data() {
    return {
      courseList: [], // 课程列表
      noticeList: [], // 所有通知列表
      selectedCourse: '', // 选中的课程ID
      userInfo: {
        id: '',
        name: '',
      },
    }
  },
  computed: {
    filteredNotices() {
      if (!this.selectedCourse) {
        return this.noticeList;
      }
      return this.noticeList.filter(notice => notice.courseId === this.selectedCourse);
    }
  },
  methods: {
    // 获取当前用户信息
    getCurrentUser() {
      const userStr = sessionStorage.getItem("CurUser");
      if (userStr) {
        const userData = JSON.parse(userStr);
        return userData.data || userData;
      }
      return null;
    },

    // 加载用户信息
    async loadUserInfo() {
      try {
        const curUser = this.getCurrentUser();
        if (!curUser || !curUser.id) {
          this.$message.error('未获取到用户信息，请重新登录');
          return;
        }
        this.userInfo = curUser;
      } catch (error) {
        console.error('获取用户信息失败：', error);
        this.$message.error('获取用户信息失败，请重新登录');
      }
    },

    // 加载学生的课程列表
    async loadCourseList() {
      try {
        const res = await this.$axios.get(
            this.$httpUrl + '/course/student/courses/' + this.userInfo.id
        );
        if (res.data.code === 200) {
          this.courseList = res.data.data;
          this.loadAllNotices();
        }
      } catch (error) {
        console.error('获取课程列表失败：', error);
        this.$message.error('获取课程列表失败');
      }
    },

    // 加载所有课程的通知
    async loadAllNotices() {
      try {
        const noticePromises = this.courseList.map(course =>
            this.$axios.get(this.$httpUrl + '/course/notices/' + course.courseId)
        );

        const responses = await Promise.all(noticePromises);

        this.noticeList = responses.reduce((acc, res, index) => {
          if (res.data.code === 200) {
            // 将课程信息添加到每个通知中
            const noticesWithCourse = res.data.data.map(notice => ({
              ...notice,
              courseName: this.courseList[index].courseName
            }));
            return [...acc, ...noticesWithCourse];
          }
          return acc;
        }, []);

        // 按发布时间排序
        this.noticeList.sort((a, b) =>
            new Date(b.publishTime) - new Date(a.publishTime)
        );
      } catch (error) {
        console.error('获取通知列表失败：', error);
        this.$message.error('获取通知列表失败');
      }
    },

    // 过滤通知
    filterNotices() {
      // 通过计算属性自动处理
    },

    // 格式化时间
    formatTime(timestamp) {
      return new Date(timestamp).toLocaleString();
    },

    // 获取通知类型（用于时间线图标）
    getNoticeType(notice) {
      const now = new Date();
      const publishTime = new Date(notice.publishTime);
      const daysDiff = Math.floor((now - publishTime) / (1000 * 60 * 60 * 24));

      if (daysDiff < 1) return 'primary';
      if (daysDiff < 3) return 'success';
      if (daysDiff < 7) return 'warning';
      return 'info';
    }
  },
  created() {
    this.loadUserInfo();
    this.loadCourseList();
  }
}
</script>

<style scoped>
.notice-center {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.notice-list {
  margin-top: 20px;
}

.notice-card {
  margin-bottom: 10px;
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.notice-header h3 {
  margin: 0;
  font-size: 18px;
}

.notice-content {
  margin: 15px 0;
  line-height: 1.6;
  color: #606266;
}

.notice-footer {
  color: #909399;
  font-size: 14px;
}

.el-timeline-item {
  padding-bottom: 20px;
}
</style>