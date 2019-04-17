<template>
    <div class="faq">
        <PageHeader title="Administration"/>
        <Box :small="true" class="content back-office" color="white">
            <div class="row">
                <div class="col m3 s12">
                    <div class="collection">
                        <router-link class="collection-item" to="/admin/users">Users<span class="badge">{{userCount}}</span>
                        </router-link>
                    </div>
                </div>
                <div class="col m9 s12">
                    <router-view></router-view>
                </div>
            </div>
        </Box>
        <TheContact id="contact"/>
    </div>
</template>


<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Box from '@/components/Box.vue';
import PageHeader from '@/components/PageHeader.vue';
import TheContact from '@/components/TheContact.vue';
import { LOAD_USERS } from '@/store/admin';

@Component({
  components: { PageHeader, Box, TheContact },
})
export default class Admin extends Vue {

  public mounted() {
    this.$store.dispatch(LOAD_USERS);
  }

  get userCount() {
    return this.$store.getters.userCount;
  }

}
</script>
