<template>
  <div class="profile">
    <PageHeader :small="true" title="Presentation"></PageHeader>
    <Box class="content" color="white">
      <div class="back-office">
        <div class="row">
          <form class="col s12">
            <div class="row">
              <div class="input-field col s12">
                <input
                  class="validate"
                  id="title"
                  type="text"
                  maxlength="255"
                  v-model="presentation.title"
                />
                <label for="title">Title</label>
              </div>
              <div class="errors" v-for="error in errors.title" :key="error">
                {{ error }}
              </div>
            </div>
            <div class="row">
              <div class="col m6 s12">
                <div class="input-field">
                  <input
                    class="autocomplete"
                    id="autocomplete-input"
                    type="text"
                    v-model="tagInput"
                  />
                  <label for="autocomplete-input">Tags</label>
                </div>
                <div>
                  <div
                    class="chip"
                    v-for="tag in presentation.tags"
                    :key="tag.name"
                  >
                    {{ tag.name }}
                    <i @click="removeTag(tag)" class="material-icons">close</i>
                  </div>
                </div>

                <div class="input-field">
                  <textarea
                    class="materialize-textarea"
                    id="shortDescription"
                    ref="shortDescription"
                    data-length="300"
                    maxlength="300"
                    v-model="presentation.shortDescription"
                  ></textarea>
                  <label for="shortDescription">Short description</label>
                  <span
                    class="helper-text"
                    data-error="wrong"
                    data-success="right"
                    >Will be used in V4P process</span
                  >
                </div>
                <div
                  class="errors"
                  v-for="error in errors.shortDescription"
                  :key="error"
                >
                  {{ error }}
                </div>
                <div class="input-field">
                  <textarea
                    class="materialize-textarea"
                    id="description"
                    ref="description"
                    data-length="1000"
                    maxlength="1000"
                    v-model="presentation.description"
                  ></textarea>
                  <label for="description">Description</label>
                </div>
                <div
                  class="errors"
                  v-for="error in errors.description"
                  :key="error"
                >
                  {{ error }}
                </div>
              </div>
              <div class="col m6 s12">
                <div class="presentation-section">
                  <label>
                    <input type="checkbox" v-model="presentation.workshop" />
                    <span>Workshop</span>
                  </label>
                </div>
                <div class="presentation-section">
                  <div>
                    <label>Language</label>
                  </div>
                  <label>
                    <input
                      name="language"
                      type="radio"
                      v-model="presentation.language"
                      value="pl"
                    />
                    <span>Polish</span>
                  </label>
                  <label>
                    <input
                      name="language"
                      type="radio"
                      v-model="presentation.language"
                      value="en"
                    />
                    <span>English</span>
                  </label>
                  <span
                    class="errors"
                    v-for="error in errors.language"
                    :key="error"
                    ><br />{{ error }}</span
                  >
                </div>

                <div class="presentation-section">
                  <div>
                    <label>Level</label>
                  </div>
                  <label>
                    <input
                      name="level"
                      type="radio"
                      v-model="presentation.level"
                      value="novice"
                    />
                    <span>Novice</span>
                  </label>
                  <label>
                    <input
                      name="level"
                      type="radio"
                      v-model="presentation.level"
                      value="intermediate"
                    />
                    <span>Intermediate</span>
                  </label>
                  <label>
                    <input
                      name="level"
                      type="radio"
                      v-model="presentation.level"
                      value="master"
                    />
                    <span>Master</span>
                  </label>
                  <span
                    class="errors"
                    v-for="error in errors.level"
                    :key="error"
                    ><br />{{ error }}</span
                  >
                </div>
              </div>
            </div>
          </form>
        </div>
        <div>
          <button
            @click="save()"
            class="waves-effect waves-light btn save-button"
          >
            Save
          </button>
          <button
            @click="cancel()"
            class="waves-effect waves-light btn cancel-button"
          >
            Cancel
          </button>
        </div>
      </div>
    </Box>
    <Contact />
  </div>
</template>
<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Contact from "@/components/Contact.vue";
import Box from "@/components/Box.vue";
import M from "materialize-css";
import { EmbeddedTags, Presentation, Tag } from "@/types";
import axios from "axios";
import PageHeader from "@/components/PageHeader.vue";

@Component({
  components: { Box, Contact, PageHeader }
})
export default class PresentationForm extends Vue {
  public $refs!: Vue["$refs"] & {
    shortDescription: Element;
    description: Element;
  };
  public presentation: Presentation = {
    title: "",
    language: "",
    level: "",
    tags: [],
    shortDescription: "",
    description: "",
    workshop: false
  };
  public tagInput = "";
  public errors: PresentationErrors = {};

  private tagsByName: { [name: string]: Tag } = {};
  private userId!: string;

  public mounted() {
    const { id, userId } = this.$route.params;
    this.userId = userId || this.$store.getters.user.jti;
    if (id) {
      axios
        .get<Presentation>(`/api/presentations/${id}`)
        .then(response => (this.presentation = response.data))
        .then(_ => axios.get<EmbeddedTags>(`/api/presentations/${id}/tags`))
        .then(tagsResponse =>
          Vue.set(this.presentation, "tags", tagsResponse.data._embedded.tags)
        )
        .then(() =>
          setTimeout(() => {
            this.setupTagsAutocomplete();
            M.textareaAutoResize(this.$refs.description);
            M.textareaAutoResize(this.$refs.shortDescription);
            M.CharacterCounter.init(this.$refs.shortDescription);
            M.CharacterCounter.init(this.$refs.description);
          })
        );
    }
  }

  public updated() {
    M.updateTextFields();
  }

  public removeTag(tag: Tag) {
    this.presentation.tags = this.presentation.tags.filter(
      it => it.id !== tag.id
    );
  }

  public save() {
    if (this.validate()) {
      axios
        .post(`/api/users/${this.userId}/presentations`, this.presentation)
        .then(() => this.$router.back());
    }
  }

  public cancel() {
    this.$router.back();
  }

  private setupTagsAutocomplete() {
    axios
      .get<EmbeddedTags>("/api/tags")
      .then(response => response.data._embedded.tags)
      .then(tags => {
        const autocompleteData: { [name: string]: null } = {};
        for (const tag of tags) {
          autocompleteData[tag.name] = null;
          this.tagsByName[tag.name] = tag;
        }
        const autocompleteElement = document.querySelectorAll(".autocomplete");
        M.Autocomplete.init(autocompleteElement, {
          minLength: 0,
          data: autocompleteData,
          onAutocomplete: arg => {
            this.tagInput = "";
            const tag = this.tagsByName[arg];
            this.presentation.tags.push(tag);
          }
        });
      });
  }

  private validate() {
    this.errors = {};
    let valid = true;
    if (this.userId === null) {
      return;
    }
    if (!this.presentation.title) {
      this.errors.title = ["Title is required"];
      valid = false;
    }
    if (!this.presentation.shortDescription) {
      this.errors.shortDescription = ["ShortDescription is required"];
      valid = false;
    }
    if (!this.presentation.description) {
      this.errors.description = ["Description is required"];
      valid = false;
    }
    if (!this.presentation.language) {
      this.errors.language = ["Language is required"];
      valid = false;
    }
    if (!this.presentation.level) {
      this.errors.level = ["Level is required"];
      valid = false;
    }
    return valid;
  }
}

interface PresentationErrors {
  title?: string[];
  shortDescription?: string[];
  description?: string[];
  language?: string[];
  level?: string[];
}
</script>
<style scoped lang="scss">
@import "../../assets/colors";

.errors {
  color: red;
}

.back-office .chip .material-icons {
  cursor: pointer;
  float: right;
  font-size: 1rem;
  line-height: 32px;
  padding-left: 8px;
}

.save-button,
.cancel-button {
  margin-right: 1rem;

  &,
  &:focus,
  &:hover {
    background-color: $brand;
  }
}

.presentation-section {
  margin-top: 1rem;
  margin-bottom: 1rem;
}
</style>
