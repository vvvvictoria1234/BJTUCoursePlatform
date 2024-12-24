import VueRouter from 'vue-router';


const routes =[
    {
        path:'/',
        name:'login',
        component:()=>import('../components/Login.vue')
    },
    {
        path:'/HomeIndex',
        name:'HomeIndex',
        component:()=>import('../components/HomeIndex'),
        // 子路由配置
        children: [
            {
                path:'/Home',
                name:'home',
                meta:{
                    title:'首页'
                },
                component:()=>import('../components/Home')
            },

        ],
    },


]
const router = new VueRouter({
    mode:'history',
    routes
})
export function resetRouter() {
    router.matcher = new VueRouter({
        mode:'history',
        routes: []
    }).matcher
}

const VueRouterPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (to) {
    return VueRouterPush.call(this, to).catch(err => err)
}
export default router;