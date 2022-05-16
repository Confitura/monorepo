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
        Submitted {{ presentationCount }} Presentations from
        {{ speakerCount }} Speakers
      </div>
    </div>

    <div class="col s12 m4">
      <div class="card-panel">
        Accepted {{ acceptedPresentationCount }} Presentations from
        {{ acceptedSpeakerCount }} Speakers
      </div>
    </div>

    <div class="col s12 m4">
      <div class="card-panel">
        There is {{ adminCount }} admins and {{ volunteerCount }} volunteers
      </div>
    </div>

    <div class="col s12 m4">
      <div class="card-panel">
        <span> Build date: {{ buildDate }} </span>
      </div>
    </div>

    <div class="col s12 m4">
      <div class="card-panel">
        <span>Tags from submitted presentations:</span>
        <ol>
          <li v-for="tag in submittedTags" :key="tag[0]">
            {{ tag[0] }} - {{ tag[1] }}
          </li>
        </ol>
      </div>
    </div>

    <div class="col s12 m4">
      <div class="card-panel">
        <span>Tags from accepted presentations:</span>
        <ol>
          <li v-for="tag in acceptedTags" :key="tag[0]">
            {{ tag[0] }} - {{ tag[1] }}
          </li>
        </ol>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import dayjs from "dayjs";
import { Presentation } from "@/types";

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

  get presentationCount() {
    return this.$store.getters.presentationCount;
  }

  get speakerCount() {
    return this.$store.getters.speakerCount;
  }

  get acceptedPresentationCount() {
    return this.$store.getters.acceptedPresentationCount;
  }

  get acceptedSpeakerCount() {
    return this.$store.getters.acceptedSpeakerCount;
  }

  get adminCount() {
    return this.$store.getters.adminCount;
  }

  get volunteerCount() {
    return this.$store.getters.volunteerCount;
  }

  get submittedTags() {
    return this.countTags(this.$store.state.admin.presentations);
  }

  get acceptedTags() {
    let presentations = this.$store.state.admin.presentations.filter(
      (it: Presentation) => it.status === "accepted"
    );

    return this.countTags(presentations);
  }

  countTags(presentations: Presentation[]) {
    let tagCountMap: Map<string, number> = presentations
      .flatMap(it => it.tags)
      .map(it => it.name)
      .reduce((acc: Map<string, number>, curr) => {
        let count = acc.get(curr) || 0;
        acc.set(curr, count + 1);
        return acc;
      }, new Map());
    return Array.from(tagCountMap).sort((l, r) => r[1] - l[1]);
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
