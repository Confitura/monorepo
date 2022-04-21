<template>
  <div class="v4p" v-hotkey="keymap">
    <Box class="content" color="black" v-if="currentVote" :full="false">
      <div v-if="!started" :key="voteIndex">
        <h2 class="v4p__header">Vote 4 Papers</h2>

        <div class="v4p__info">
          <p>
            The moment you were waiting for is here! Vote 4 Papers is open. You
            have almost 100 presentations to choose from.
          </p>
          <p>
            For each and every presentation you can either say that you are very
            interested in seeing it (+1), not interested at all (-1), or you
            don't mind it (0).
          </p>
          <p>
            Since we know that you are very busy, we have asked our speakers to
            provide TL;DR version of the description of their presentations (max
            300 characters). If it's not enough for you to make a decision you
            can switch to full description.
          </p>

          <p>We are waiting for your votes till end of Sunday, May 26</p>

          <p>
            btw. you can also vote with keyboard shortcuts. Press ? to list of
            hotkeys.
          </p>
        </div>
        <div class="v4p__start-button" @click="start()">
          start
        </div>
      </div>
      <div class="v4p-container" v-if="started" :key="voteIndex">
        <div v-if="presentation" class="presentation">
          <h2 class="presentation__title">{{ presentation.title }}</h2>

          <div class="presentation__speakers">
            <div
                class="presentation__speaker speaker"
                v-for="speaker in presentation.speakers"
                :key="speaker.name"
            >
              <LazyImage :src="speaker.photo" class="speaker__photo"/>
              <div class="speaker__name">
                <span>{{ getFirstNameOf(speaker) }}</span>
                <span>{{ getLastNameOf(speaker) }}</span>
              </div>
            </div>
          </div>
          <div class="presentation__metadata">
            <div class="presentation__metadata-group">
              <i class="presentation__icon fas fa-flag" title="language"></i>
              <div class="presentation__language">{{ language }}</div>
            </div>
            <div class="presentation__metadata-group">
              <i
                  class="presentation__icon fas fa-graduation-cap"
                  title="level"
              ></i>
              <div class="presentation__level">{{ presentation.level }}</div>
            </div>
            <div
                class="presentation__metadata-group"
                v-if="presentation.workshop"
            >
              <i class="presentation__icon fas fa-hammer" title="workshop"></i>
              <div class="presentation__workshop">workshop</div>
            </div>
            <div class="presentation__metadata-group">
              <template v-if="hasTags">
                <i class="presentation__icon  fas fa-tags" title="tags"></i>
                <div class="presentation__tags">
                  <span
                      class="presentation__tag"
                      v-for="tag in presentation.tags"
                      :key="tag.name"
                  >{{ tag.name }}</span
                  >
                </div>
              </template>
            </div>
          </div>
          <div class="presentation__description-type description-type">
            <span
                class="description-type__item"
                :class="{
                'description-type__item--active': descriptionType === 'short'
              }"
                @click="descriptionType = 'short'"
            >short description</span
            >
            <span
                class="description-type__item"
                :class="{
                'description-type__item--active': descriptionType === 'full'
              }"
                @click="descriptionType = 'full'"
            >full description</span
            >
          </div>
          <div class="presentation__description">{{ description }}</div>
        </div>
        <aside class="side">
          <div class="vote">
            <div
                class="vote-item"
                v-for="rate in rateValues"
                :key="rate.rate"
                :class="{
                [`vote-item--${rate.name}`]: true,
                'vote-item--active': isActive(rate.rate)
              }"
                :disabled="currentVote.rate === rate.rate"
                @click="doVote(rate.rate)"
            >
              <span>{{ rate.label }}</span>
            </div>
          </div>
          <div class="previous" @click="previous()">
            <i class="fas fa-chevron-left"></i>
          </div>
          <div class="vote__counter">{{ voteIndex + 1 }} / {{ total }}</div>
          <div class="next" @click="next()">
            <i class="fas fa-chevron-right"></i>
          </div>
        </aside>
      </div>
    </Box>
    <Box class="content" color="black" v-else>
      <h2 class="v4p__header">Thank you!</h2>
      <div class="v4p_summary">
        <p>
          For going through all presentations. We know it was not easy :)
        </p>
        <p>
          If fore some reason you want to review your votes, just click a button
          below
        </p>
      </div>

      <div class="v4p__start-again-button" @click="startAgain()">
        start again
      </div>
    </Box>
  </div>
</template>

<script lang="ts">
import {Component, Vue} from "vue-property-decorator";
import LazyImage from "../components/LazyImage.vue";
import {LOAD_VOTES, SAVE_VOTE} from "@/store/vote4papers";
import {Presentation, UserProfile, Vote} from "@/types";
import VueScrollTo from "vue-scrollto";
import axios from "axios";

import {defineComponent, Ref, ref} from "vue";
import PageHeader from "@/components/PageHeader.vue";
import Contact from "@/components/Contact.vue";
import Box from "@/components/Box.vue";

export default defineComponent({
  name: "Vote4Papers",
  components: {PageHeader, Box, Contact, VueScrollTo, LazyImage},
  setup() {
    const rateValues = [
      {rate: +1, label: "+1", name: "positive"},
      {rate: 0, label: "0", name: "neutral"},
      {rate: -1, label: "-1", name: "negative"}
    ];
    const presentation: Presentation | null = null;
    const voteIndex = 0;
    const descriptionType: "short" | "full" = "short";
    return {voteIndex}
  },
  mounted() {
    const voteIndex = localStorage.getItem("VOTE_INDEX");
    if (voteIndex != null) {
      this.voteIndex = Number(voteIndex);
    }
    this.$store
        .dispatch(LOAD_VOTES)
        .then(() => this.loadPresentationFor(this.currentVote));
  }
});

@Component({
  components: {PageHeader, Box, Contact, VueScrollTo, LazyImage}
})
class Vote4Papers extends Vue {
  public rateValues = [
    {rate: +1, label: "+1", name: "positive"},
    {rate: 0, label: "0", name: "neutral"},
    {rate: -1, label: "-1", name: "negative"}
  ];
  public presentation: Presentation | null = null;
  public voteIndex = 0;
  public descriptionType: "short" | "full" = "short";

  public mounted() {
    const voteIndex = localStorage.getItem("VOTE_INDEX");
    if (voteIndex != null) {
      this.voteIndex = Number(voteIndex);
    }
    this.$store
        .dispatch(LOAD_VOTES)
        .then(() => this.loadPresentationFor(this.currentVote));
  }

  get currentVote(): Vote | null {
    if (this.voteIndex < this.votes.length) {
      return this.votes[this.voteIndex];
    } else {
      return null;
    }
  }

  get votes(): Vote[] {
    return this.$store.state.v4p.votes;
  }

  get total(): number {
    return this.votes.length;
  }

  get description(): string {
    if (this.presentation) {
      return this.descriptionType === "short"
          ? this.presentation.shortDescription
          : this.presentation.description;
    } else {
      return "";
    }
  }

  get started(): boolean {
    return localStorage.getItem("started") !== null;
  }

  public start(): void {
    localStorage.setItem("started", "true");
    location.reload();
  }

  public next(): void {
    if (this.currentVote && this.voteIndex < this.total) {
      this.doVote(this.currentVote.rate, +1);
    }
  }

  public previous(): void {
    if (this.currentVote && this.voteIndex > 0) {
      this.doVote(this.currentVote.rate, -1);
    }
  }

  public doVote(rate: number, direction = 1) {
    const vote = this.currentVote;
    if (vote == null) {
      return;
    }
    vote.rate = rate;
    this.$store.dispatch(SAVE_VOTE, {vote}).then(() => {
      this.changePage(this.voteIndex + direction);
      window.scrollTo(0, 0);
    });
  }

  public startAgain() {
    this.changePage(0);
  }

  public isActive(rate: number) {
    return this.currentVote && this.currentVote.rate === rate;
  }

  get hasTags(): boolean | null {
    return this.presentation && this.presentation.tags.length > 0;
  }

  get language(): string {
    let language = "polish";
    if (this.presentation && this.presentation.language === "en") {
      language = "english";
    }
    return language;
  }

  public getFirstNameOf(speaker: UserProfile): string {
    const name = speaker.name || "";
    const idx = name.indexOf(" ");
    return name.substring(0, idx);
  }

  public getLastNameOf(speaker: UserProfile): string {
    const name = speaker.name || "";
    const idx = name.indexOf(" ");
    return name.substring(idx);
  }

  private changePage(pageNumber: number): void {
    this.voteIndex = pageNumber;
    localStorage.setItem("VOTE_INDEX", `${pageNumber}`);
    this.loadPresentationFor(this.currentVote);
  }

  private loadPresentationFor(vote: Vote | null) {
    if (vote == null) {
      this.presentation = null;
    } else {
      axios
          .get<Presentation>(`/api/votes/${vote.id}/presentation`, {
            params: {projection: "inlineSpeaker"}
          })
          .then(it => (this.presentation = it.data));
    }
  }

  private toggleDescription() {
    this.descriptionType = this.descriptionType === "short" ? "full" : "short";
  }

  get keymap() {
    return {
      space: () => this.toggleDescription(),
      enter: () => {
        if (this.currentVote) {
          this.start();
        } else {
          this.startAgain();
        }
      },
      up: () => this.changeVote(1),
      w: () => this.changeVote(1),
      down: () => this.changeVote(-1),
      s: () => this.changeVote(-1),
      left: () => this.previous(),
      a: () => this.previous(),
      right: () => this.next(),
      d: () => this.next(),
      "shift+/": () => this.showHelp()
    };
  }

  private showHelp() {
    this.$toasted.info(
        "<pre>" +
        "shortcuts:\n\n" +
        "enter     -> start\n" +
        "space     -> toggle description\n" +
        "?         -> self\n\n" +
        "w | up    -> +1\n" +
        "s | down  -> -1\n" +
        "d | right -> next\n" +
        "a | left  -> go back\n\n" +
        "</pre>",
        {
          duration: 5000,
          position: "bottom-right"
        }
    );
  }

  private changeVote(value: number) {
    const currentVote = this.currentVote;
    if (currentVote != null) {
      let rate = (currentVote.rate || 0) + value;
      if (rate < -1) {
        rate = 1;
      } else if (rate > 1) {
        rate = -1;
      }
      currentVote.rate = rate;
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../assets/colors";
@import "../assets/sizes";
@import "../assets/media";
@import "../assets/fonts";

.v4p {
  background-color: #000000;
  background-image: url("../assets/stars.png");
  background-repeat: repeat;
  min-height: 100vh;
  box-sizing: border-box;
}

.v4p-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #ffffff;
  margin-top: 2rem;
  padding-bottom: 7rem;
  @include md() {
    flex-direction: row;
    padding-bottom: 0;
  }
}

.side {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  /*@formatter:off*/
  grid-template-areas:
    "counter counter counter"
    "prev    vote    next";
  /*@formatter:on*/

  flex-direction: column-reverse;
  position: fixed;
  bottom: 0;
  background-color: #000000;
  box-sizing: border-box;
  width: 100vw;
  align-self: center;
  padding-top: 0.5rem;
  border-top: 1px #4a4a4a solid;
  @include md() {
    padding-top: 0;
    background: transparent;
    position: unset;
    flex-direction: column;
    width: unset;
    align-self: start;
    /*@formatter:off*/
    grid-template-areas:
      ".    vote    ."
      "prev counter next";
    /*@formatter:on*/
    border-top: none;
  }
}

.previous {
  grid-area: prev;
}

.next {
  grid-area: next;
}

.next,
.previous {
  align-self: center;
  justify-self: center;
  font-size: 2rem;
  cursor: pointer;

  &:hover {
    color: $brand;
  }
}

.vote {
  grid-area: vote;
  padding: 1rem;
  display: flex;
  flex-direction: row-reverse;
  justify-content: center;
  @include md() {
    flex-direction: column;
  }

  &__counter {
    grid-area: counter;
    font-size: 1.2rem;
    font-weight: bold;
    justify-self: center;
    align-self: center;
    color: #4a4a4a;
  }
}

.vote-item {
  width: 60px;
  height: 60px;
  background-color: #ffffff;
  color: #000000;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  font-size: 1.5rem;
  margin: 1px;
  cursor: pointer;

  &:hover,
  &--active {
    background-color: $brand;
    color: #ffffff;
  }

  &--positive {
    border-radius: 0 30px 30px 0;
    @include md() {
      border-radius: 30px 30px 0 0;
    }
  }

  &--negative {
    border-radius: 30px 0 0 30px;
    @include md() {
      border-radius: 0 0 30px 30px;
    }
  }
}

.presentation {
  @include md() {
    width: 840px;
  }

  &__title {
    color: $brand;
    margin-top: 0;
    font-size: 2rem;
    @include md() {
      font-size: 3rem;
    }
  }

  &__metadata {
    display: flex;
    flex-wrap: wrap;
    font-size: 1.5rem;
    margin-bottom: 2rem;
  }

  &__metadata-group {
    display: flex;
    margin-bottom: 0.5rem;
  }

  &__icon {
    margin-right: 1rem;
  }

  &__language,
  &__level,
  &__workshop {
    margin-right: 1rem;
  }

  &__tag {
    &:not(:last-child)::after {
      content: ", ";
    }
  }

  &__speakers {
    display: flex;
    flex-direction: column;
    @include md() {
      flex-direction: row;
    }
  }

  &__description-type {
    margin-bottom: 1rem;
    color: #4a4a4a;
  }

  &__description {
    font-size: 1.2rem;
    line-height: 1.5rem;
    white-space: pre-wrap;
  }
}

.description-type__item {
  margin-right: 1rem;
  cursor: pointer;
  font-weight: bold;

  &:hover,
  &--active {
    color: #ffffff;
    text-decoration: underline;
  }
}

.speaker {
  display: flex;
  justify-items: center;
  align-items: center;
  margin-right: 3rem;
  margin-bottom: 2rem;
  @include md() {
    margin-bottom: 3rem;
  }

  &__photo {
    flex-grow: 0;
    width: 85px;
    height: 85px;
    object-fit: cover;
    border-radius: 85px;
    border: 5px solid #979797;
    margin-right: 1rem;
    background-color: #000000;
    /*background-image: url('../assets/astronaut.svg');*/
    /*background-repeat: no-repeat;*/
    /*background-size: cover;*/
  }

  &__name {
    display: flex;
    flex-direction: column;
    color: $brand;
    font-size: 1.5rem;
    font-weight: bold;
  }
}

.v4p__header {
  color: $brand;
  font-family: $font-bold;
  font-size: 3rem;
}

.v4p__info,
.v4p_summary {
  color: #ffffff;
  font-size: 1.2rem;
  line-height: 1.4;
}

.v4p__start-button,
.v4p__start-again-button {
  background-color: $brand;
  color: #ffffff;
  font-family: $font-bold;
  font-size: 2rem;
  padding-top: 1rem;
  padding-bottom: 1rem;
  text-align: center;
  border-radius: 2rem;
  text-transform: uppercase;
  cursor: pointer;
  max-width: 300px;
  @include md() {
    margin-top: 3rem;
  }
}
</style>
