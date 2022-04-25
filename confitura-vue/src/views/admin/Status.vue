<template>
  <div class="row">
    <div class="col s12 m4">
      <div class="card-panel" v-bind:class="{ opened: c4pEnabled, closed: !c4pEnabled }">
        <span class="white-text">
          C4P is now {{ c4pState }} ({{ c4pStart }} to {{ c4pEnd }})
        </span>
      </div>
    </div>

    <div class="col s12 m4">
      <div class="card-panel">
        <span>
          Build date: {{ buildDate }}
        </span>
      </div>
    </div>

  </div>

</template>

<script>

import {Component, Vue} from "vue-property-decorator";
import dayjs from "dayjs";

const dateFormat = "DD.MM.YYYY HH:mm";

@Component({})
export default class Status extends Vue {

  get c4pEnabled() {
    let info = this.$store.getters.info;
    if (info && info.c4p) {
      return info.c4p.enabled
    } else {
      return false;
    }
  }

  get info() {
    return this.$store.getters.info;
  }

  get buildDate() {
    if (this.info.build) {
      return dayjs(this.info.build.time).format(dateFormat)
    }
  }


  get c4pState() {
    return this.c4pEnabled ? "opened" : "closed";
  }

  get c4pStart() {
    let info = this.info;
    if (info && info.c4p)
      return dayjs(info.c4p.start).format(dateFormat)
  }

  get c4pEnd() {
    let info = this.info;
    if (info && info.c4p)
      return dayjs(info.c4p.start).format(dateFormat)
  }

}
</script>

<style scoped lang="scss">

.opened {
  background-color: hsl(120, 100%, 25%);
}

.closed {
  background-color: hsl(358, 100%, 25%);
}
</style>
