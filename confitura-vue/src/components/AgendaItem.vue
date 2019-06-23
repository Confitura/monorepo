<template>
    <div class="agendaItem">
        <div v-if="entry.roomLabel" class="agendaItem__room">{{entry.roomLabel}}</div>
        <div v-if="entry.presentationId">
            <div class="agendaItem__title">{{entry.presentation.title}}</div>
            <div class="agendaItem__speakers">
                <span class="agendaItem__speaker" v-for="speaker in entry.speaker">{{speaker.name}}</span>
            </div>
        </div>
        <div v-else class="agendaItem__label">
            {{entry.label}}
        </div>
    </div>
</template>

<script lang="ts">
  import { Component, Prop, Vue } from 'vue-property-decorator';
  import { AgendaEntry } from '@/views/Agenda.vue';

  @Component({})
  export default class AgendaItem extends Vue {
    @Prop({ required: true })
    public entry!: AgendaEntry;


  }
</script>

<style scoped lang="scss">
    @import "../assets/sizes";
    @import "../assets/media";
    @import "../assets/colors";

    .agendaItem {
        font-size: 1rem;
        padding: .7rem;
        display: flex;
        flex-direction: column;

        @include md() {
            padding: 1.5rem;
        }

        &__room {
            color: $brand;
            font-size: .8rem;
            margin-bottom: .5rem;
            @include md() {
                display: none;
            }
        }

        &__entry--all, &__label {
            color: $brand;
            font-weight: bold;
            font-size: 1.2rem;
            text-transform: capitalize;
        }

        &__label {
            justify-self: center;
        }

    }

    .agendaItem__speakers {
        font-size: 1rem;
        color: #767676;
        margin-top: 1rem;
    }

    .agendaItem__speaker {

        &:not(:last-child):after {
            content: ', ';
        }
    }
</style>
