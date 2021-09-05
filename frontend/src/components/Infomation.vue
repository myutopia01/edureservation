<template>

<v-data-table
    :headers="headers"
    :items="infomation"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'Infomation',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        headers: [
            { text: "id", value: "id" },
            { text: "orderId", value: "orderId" },
            { text: "orderStatus", value: "orderStatus" },
            { text: "payStatus", value: "payStatus" },
            { text: "reservationStatus", value: "reservationStatus" },
        ],
        infomation : [],
    }),
    async created() {
      var temp = await axios.get(axios.backend + '/infomations')

      this.infomation = temp.data._embedded.infomations;

    },
    methods: {
    }
  }
</script>

