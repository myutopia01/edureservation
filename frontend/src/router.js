
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import OrderManager from "./components/OrderManager"

import PayManager from "./components/PayManager"

import ReservationManager from "./components/ReservationManager"


import Infomation from "./components/Infomation"
export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/orders',
                name: 'OrderManager',
                component: OrderManager
            },

            {
                path: '/pays',
                name: 'PayManager',
                component: PayManager
            },

            {
                path: '/reservations',
                name: 'ReservationManager',
                component: ReservationManager
            },


            {
                path: '/infomations',
                name: 'Infomation',
                component: Infomation
            },


    ]
})
