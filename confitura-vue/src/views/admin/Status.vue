<template>
  <div class="row">
    <div class="col s12 m4">
      <div class="card-panel" v-bind:class="[c4pState]">
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
  get info() {
    return this.$store.getters.info;
  }

  get buildDate() {
    if (this.info.build) {
      return dayjs.unix(this.info.build.time).format(dateFormat);
    } else {
      return null;
    }
  }

  get c4pState() {
    let info = this.$store.getters.info;
    let state = false;
    if (info && info.c4p) {
      state = info.c4p.enabled;
    }
    return state ? "opened" : "closed";
  }

  get c4pStart() {
    let info = this.info;
    if (info && info.c4p) {
      return dayjs.unix(info.c4p.start).format(dateFormat);
    } else {
      return null;
    }
  }

  get c4pEnd() {
    let info = this.info;
    if (info && info.c4p) {
      return dayjs.unix(info.c4p.end).format(dateFormat);
    } else {
      return null;
    }
  }
}
</script>

<style scoped lang="scss">
.opened {
  background-color: hsl(120, 100%, 25%);
}

.closed {
  background-color: hsl(0, 0%, 25%);
}
</style>
