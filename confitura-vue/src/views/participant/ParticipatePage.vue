<template>
  <div class="particiate">
    <PageHeader title="Registration" type="peace"></PageHeader>
    <Box class="content " color="white" :full="false">
      <div v-if="!form">
        <h2 class="error__header">Ups.... something is wrong...</h2>
        <p class="error__message">
          <span v-if="loadError.reason === 'INVALID'">
            We cannot find this voucher. Please make sure that the URL is
            correct.
          </span>
          <span v-else-if="loadError.reason === 'TAKEN'">
            Looks like this voucher is already registered. We cannot tell you by
            whom (GDPR and stuff...), but we can give you a hint that registered
            email is something like:
            <strong>{{ loadError.additionalInfo }}</strong
            >.
          </span>
          <span v-else>
            But we dont know what... don't worry, it's not you - it's us. Try
            one more time (or maybe even 3) - if it doesn't help then contact
            us!
          </span>
        </p>
      </div>
      <div v-else>
        <h4 class="participate__info">All fields required</h4>
        <h2 class="participate__title">Personal information</h2>
        <div class="row">
          <form @submit="save" class="col s12" novalidate>
            <div class="row">
              <md-field :class="{ 'md-invalid': errors.has('firstName') }">
                <label>first name</label>
                <md-input
                  v-model="form.firstName"
                  required
                  v-validate="'required'"
                  name="firstName"
                  maxlength="100"
                ></md-input>
                <span class="md-error">{{ errors.first("firstName") }}</span>
              </md-field>
            </div>
            <div class="row">
              <md-field :class="{ 'md-invalid': errors.has('last name') }">
                <label>last name</label>
                <md-input
                  v-model="form.lastName"
                  required
                  v-validate="'required'"
                  name="last name"
                  maxlength="100"
                ></md-input>
                <span class="md-error">{{ errors.first("last name") }}</span>
              </md-field>
            </div>
            <div class="row">
              <md-field :class="{ 'md-invalid': errors.has('e-mail address') }">
                <label>e-mail address</label>
                <md-input
                  type="email"
                  v-model="form.email"
                  required
                  v-validate="'required|email'"
                  name="e-mail address"
                  maxlength="100"
                ></md-input>
                <span class="md-error">{{
                  errors.first("e-mail address")
                }}</span>
              </md-field>
            </div>
            <div class="row">
              <md-field :class="{ 'md-invalid': errors.has('t-shirt cut') }">
                <label for="t-shirt-cut">t-shirt cut</label>
                <md-select
                  v-model="form.gender"
                  name="t-shirt cut"
                  id="t-shirt-cut"
                  required
                  v-validate="'required'"
                >
                  <md-option value="m">male</md-option>
                  <md-option value="f">female</md-option>
                </md-select>
                <span class="md-error">{{ errors.first("t-shirt cut") }}</span>
              </md-field>
            </div>
            <div class="row">
              <div class="input-field col s12">
                <md-field :class="{ 'md-invalid': errors.has('t-shirt size') }">
                  <label for="t-shirt-size">t-shirt size</label>
                  <md-select
                    v-model="form.size"
                    name="t-shirt size"
                    id="t-shirt-size"
                    required
                    v-validate="'required'"
                  >
                    <md-option value="S">S</md-option>
                    <md-option value="M">M</md-option>
                    <md-option value="L">L</md-option>
                    <md-option value="XL">XL</md-option>
                    <md-option value="XXL">XXL</md-option>
                  </md-select>
                  <span class="md-error">{{
                    errors.first("t-shirt size")
                  }}</span>
                </md-field>
              </div>
            </div>
            <div class="row">
              <strong>Sizes: </strong>
              <a
                href="https://share.adler.info/files//label/110---product_size.pdf"
                >male</a
              >,
              <a
                href="https://share.adler.info/files//label/122---product_size.pdf"
                >female</a
              >
            </div>
            <div class="row">
              <h2 class="participate__title">Tell us more about you</h2>
              <div class="participate__statisticsInfo">
                This data will be used for statistics only, and will not be
                associated with your personal profile. Thanks to them we will be
                able to better tailor our agenda to the needs of our
                participants in future editions.
              </div>
            </div>

            <div class="row">
              <md-field :class="{ 'md-invalid': errors.has('meal option') }">
                <label for="meal-option">meal option</label>
                <md-select
                  v-model="form.mealOption"
                  name="meal option"
                  id="meal-option"
                  required
                  v-validate="'required'"
                >
                  <md-option value="meat">meat</md-option>
                  <md-option value="vegetarian">vegetarian</md-option>
                </md-select>

                <span class="md-error">{{ errors.first("meal option") }}</span>
              </md-field>
            </div>
            <div class="row">
              <strong>Food note:</strong> We can't guarantee that you will be
              able to get option that you have selected. The food at confitura
              will not be portioned, so first come - first served.
            </div>
            <div class="row">
              <md-field :class="{ 'md-invalid': errors.has('city') }">
                <label>city</label>
                <md-input
                  v-model="form.city"
                  maxlength="255"
                  required
                  v-validate="'required'"
                  name="city"
                ></md-input>
                <span class="md-error">{{ errors.first("city") }}</span>
              </md-field>
            </div>
            <div class="row">
              <md-field :class="{ 'md-invalid': errors.has('experience') }">
                <label for="experience">your experience</label>
                <md-select
                  v-model="form.experience"
                  name="experience"
                  id="experience"
                  required
                  v-validate="'required'"
                >
                  <md-option value="I am just learning"
                    >I am just learning</md-option
                  >
                  <md-option value="Less than a year"
                    >Less than a year</md-option
                  >
                  <md-option value="1-2 years">1-2 years</md-option>
                  <md-option value="2-4 years">2-4 years</md-option>
                  <md-option value="4-8 years">4-8 years</md-option>
                  <md-option value="Over 8 years">Over 8 years</md-option>
                </md-select>
                <span class="md-error">{{ errors.first("experience") }}</span>
              </md-field>
            </div>
            <div class="row">
              <md-field :class="{ 'md-invalid': errors.has('experience') }">
                <label for="role">your role</label>
                <md-select
                  v-model="form.role"
                  name="role"
                  id="role"
                  required
                  v-validate="'required'"
                >
                  <md-option value="student">Student</md-option>
                  <md-option value="Developer">Developer</md-option>
                  <md-option value="Team Leader">Team Leader</md-option>
                  <md-option value="Project Leader">Project Leader</md-option>
                  <md-option value="Architect">Architect</md-option>
                  <md-option value="Tester">Tester</md-option>
                  <md-option value="Other">Other</md-option>
                </md-select>
                <span class="md-error">{{ errors.first("role") }}</span>
              </md-field>
            </div>
            <div class="row">
              <md-chips
                md-clickable
                md-check-duplicated
                v-model="form.technologies"
                md-placeholder="Technologies..."
                @md-insert="refreshAvailableTech()"
                @md-delete="refreshAvailableTech()"
              ></md-chips>
            </div>
            <div class="row">
              <div>
                <md-chip
                  md-clickable
                  v-for="tech in availableTechnologies"
                  :key="tech"
                  @click="addTech(tech)"
                  >{{ tech }}
                </md-chip>
              </div>
            </div>
            <div class="row">
              <md-checkbox
                v-model="form.privacyPolicyAccepted"
                required
                v-validate="'required'"
                name="privacy policy"
                >I accept the
                <router-link to="privacy-policy">privacy policy</router-link>
              </md-checkbox>
              <div class="error" v-if="errors.has('privacy policy')">
                Privacy Policy has to be accepted
              </div>
            </div>
            <div class="row">
              <md-button
                type="submit"
                class="md-raised md-accent"
                :disabled="savingInProgress"
                >Save</md-button
              >
            </div>
          </form>
        </div>
      </div>
    </Box>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Box from "@/components/Box.vue";
import Contact from "@/components/Contact.vue";
import axios, { AxiosError } from "axios";
import PageHeader from "@/components/PageHeader.vue";
import { Participant, RegistrationForm } from "@/types";
import {
  MdButton,
  MdCheckbox,
  MdChips,
  MdField,
  MdList,
  MdMenu,
  MdRadio
} from "vue-material/dist/components";
import "vue-material/dist/vue-material.min.css";

Vue.use(MdButton);
Vue.use(MdField);
Vue.use(MdRadio);
Vue.use(MdMenu);
Vue.use(MdList);
Vue.use(MdCheckbox);
Vue.use(MdChips);
@Component({
  components: { PageHeader, Box, Contact }
})
export default class ParticipatePage extends Vue {
  public form: RegistrationForm | null = null;
  private allTechnologies = [
    "Java", "Scala", "Kotlin", "Groovy",
    "Spring", "JakartaEE", "Microprofile",
    "JUnit", "TestNG", "Mockito", "Spock",
    "Go", "Rust", "Python", "SQL", "NoSQL",
    "GraalVM", "Docker", "Kubernetes",
  ];
  public availableTechnologies: String[] = this.allTechnologies;
  public loadError: ResponseError | null = null;
  public savingInProgress = false;

  public mounted() {
    const { voucher } = this.$route.query;
    axios
      .get(`/api/vouchers/${voucher}/check`)
      .then(() => {
        this.form = { voucher: { id: voucher as string }, technologies: [] };
      })
      .catch((error: AxiosError) => {
        if (error.response) {
          this.loadError = error.response.data;
        }
      });
  }

  async addTech(tech: string) {
    if (this.form) {
      this.form.technologies.push(tech);
      this.refreshAvailableTech();
      await this.$nextTick();
    }
  }

  refreshAvailableTech() {
    if (this.form) {
      const used = this.form.technologies || [];
      this.availableTechnologies = this.allTechnologies.filter(it => used.indexOf(it) < 0)
    }
  }

  public async save(event: Event) {
    event.preventDefault();
    const isValid = await this.$validator.validate();
    if (isValid) {
      this.savingInProgress = true;
      axios
        .post<Participant>("/api/participants", this.form)
        .then(it => {
          const id: string = it.data.id as string;
          this.$router.push({ name: "participant", params: { id } });
        })
        .finally(() => (this.savingInProgress = false));
    }
  }
}

interface ResponseError {
  reason: string;
  additionalInfo: string;
}
</script>

<style lang="scss" scoped>
@import "../../assets/colors";
@import "../../assets/sizes";
@import "../../assets/media";
@import "../../assets/fonts";

.back-office {
  padding-top: 10vh;
}

.errors {
  color: red !important;
}

.participate__info {
  font-weight: bold;
  font-size: 1.5rem;
}

.participate__title {
  color: $brand;
  font-weight: bold;
  font-size: 2.5rem;
  line-height: 2.7rem;
}

.participate__statisticsInfo {
  font-size: 1.2rem;
  line-height: 1.4rem;
}

.error {
  color: #ff1744;
  font-size: 12px;
}

.error__header {
  font-size: 2.5rem;
  font-weight: bold;
  color: #ff1744;
}

.error__message {
  font-size: 1.2rem;
  padding-left: 4rem;
}
</style>
