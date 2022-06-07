<template>

  <div class="allegro">
    <a v-if="!status.isAuthorized" class="waves-effect waves-light btn" id="authorize-button"
       href="/api/allegro/authorize">authorize in allegro</a>
    <a v-if="status.isAuthorized" class="waves-effect waves-light btn" id="ready-to-send" target="_blank"
       href="/api/allegro/report/ready-to-send">get ready to send</a>

  </div>
</template>

<script lang="ts">
import {Component, Vue} from "vue-property-decorator";
import axios from "axios";

@Component({
  components: {}
})
export default class Allegro extends Vue {
  status: { isAuthorized?: boolean } = {};
  query: any = {};

  mounted() {
    const {code, state} = this.$route.query;
    if (code && state) {
      const params = {code, state}
      axios.get("/api/allegro/callback", {params})
          .then(it => this.$router.replace({query: {}}))
          .then(it => this.refreshState())
    } else {
      this.refreshState();
    }
  }

  private refreshState() {
    return axios
        .get("/api/allegro/status")
        .then(it => (this.status = it.data));
  }
}
</script>
<style>
#authorize-button {
  color: white !important;
}

#ready-to-send {
  color: white !important;
}
</style>
