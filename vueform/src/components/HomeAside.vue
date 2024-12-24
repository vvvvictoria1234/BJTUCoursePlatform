<script >
export default {
  name: "Aside",
  data(){
    return {
      //isCollapse:false

      /* menu:[
           {
               menuClick:'Admin',
               menuName:'管路员管理',
               menuIcon:'el-icon-s-custom'
           },{
               menuClick:'User',
               menuName:'用户管理',
               menuIcon:'el-icon-user-solid'
           }
       ]*/
    }
  },
  computed:{
    "menu":{
      get(){
        return this.$store.state.menu
      }
    }
  },

  props:{
    isCollapse:Boolean
  }
}

</script>

<template>
  <el-menu  style="height: 100vh;"
            background-color="#545c64"
            text-color="#fff"
            active-text-color="#ffd04b"
            default-active="/Home"
            router>
    <el-menu-item index="/Home">
      <i class="el-icon-s-home"></i>
      <span slot="title">个人中心</span>
    </el-menu-item>

    <el-menu-item :index="'/'+item.menuclick" v-for="(item,i) in menu" :key="i">
      <i :class="item.menuicon"></i>
      <span slot="title">{{item.menuname}}</span>
    </el-menu-item>



  </el-menu>
</template>

<style scoped>

</style>