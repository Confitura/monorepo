<template>
  <div class="admin">
    <PageHeader title="Administration" :small="true" />
    <Box :small="true" class="content back-office" color="white">
      <div class="row">
        <div>
          <div class="admin__menu">
            <div class="admin__menu-item">
              <router-link to="/admin/users">Users </router-link>
              <span class=" new badge">{{ userCount }}</span>
            </div>
            <div class="admin__menu-item">
              <router-link to="/admin/presentations"
                >Presentations
              </router-link>
              <span class="new badge">{{ presentationCount }}</span>
            </div>
            <div class="admin__menu-item">
              <router-link to="/admin/vouchers">Vouchers </router-link>
              <span class=" new badge">{{ voucherCount }}</span>
            </div>
          </div>
        </div>
      </div>
      <div>
        <router-view></router-view>
      </div>
    </Box>
    <Contact />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Box from "@/components/Box.vue";
import PageHeader from "@/components/PageHeader.vue";
import Contact from "@/components/Contact.vue";
import {
  LOAD_USERS,
  LOAD_ALL_PRESENTATIONS,
  LOAD_VOUCHERS
} from "@/store/admin";

@Component({
  components: { PageHeader, Box, Contact }
})
export default class Admin extends Vue {
  public mounted() {
    this.$store.dispatch(LOAD_USERS);
    this.$store.dispatch(LOAD_ALL_PRESENTATIONS);
    this.$store.dispatch(LOAD_VOUCHERS);
  }

  get userCount() {
    return this.$store.getters.userCount;
  }

  get presentationCount() {
    return this.$store.getters.presentationCount;
  }

  get voucherCount() {
    return this.$store.getters.vouchersCount;
  }
}
</script>

<style scoped lang="scss">
.admin {
  &__menu {
    display: flex;
  }
  &__menu-item {
    display: flex;
    align-items: center;
    margin-right: 1rem;
  }
}

.back-office span.badge.new:after {
  content: "";
}

.back-office span.badge {
  min-width: 2rem;
}
</style>
