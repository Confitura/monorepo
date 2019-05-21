<template>
    <div class="users">
        <table>
            <thead>

            <tr>
                <th>id</th>
                <th>name</th>
                <th>email</th>
                <th>admin</th>
                <th>volunteer</th>
                <th>speaker</th>
                <th>Privacy Accepted</th>
            </tr>
            </thead>
            <tr v-for="user of users">
                <td>
                    <router-link :to="{name: 'profile', params: {id: user.id} }">{{user.id}}</router-link>
                </td>
                <td>{{user.name}}</td>
                <td>{{user.email}}</td>
                <td>{{user.admin}}</td>
                <td>{{user.volunteer}}</td>
                <td>{{user.speaker}}</td>
                <td>{{user.privacyPolicyAccepted}}</td>
            </tr>
        </table>
    </div>
</template>


<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';

  @Component({
    components: {},
  })
  export default class Users extends Vue {

    get users() {
      return this.$store.state.admin.users;
    }

  }
</script>
