<template>
    <div class="presentations">
        <table>
            <thead>

            <tr>
                <td>id</td>
                <td>title</td>
                <td>language</td>
                <td>level</td>
                <td>speakers</td>
                <td>tags</td>
                <td>status</td>
            </tr>
            </thead>
            <tr v-for="presentation of presentations">
                <td>
                    <router-link
                            :to="{name: 'presentation', params:{id:presentation.id, userId:presentation.speakers[0].id}}">
                        {{presentation.id}}
                    </router-link>

                </td>
                <td>{{presentation.title}}</td>
                <td>{{presentation.language}}</td>
                <td>{{presentation.level}}</td>
                <td>
                    <span v-for="speaker of presentation.speakers">{{speaker.name}}</span>
                </td>
                <td>
                    <span class="badge" v-for="tag of presentation.tags">{{tag.name}}</span>
                </td>
                <td>{{presentation.status}}</td>
            </tr>
        </table>
    </div>
</template>


<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';

  @Component({
    components: {},
  })
  export default class Presentations extends Vue {

    get presentations() {
      return this.$store.state.admin.presentations;
    }

  }
</script>
