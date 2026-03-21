import {defineStore} from 'pinia'

const partners: Partner[] = getPartners();
export const usePartnersStore = defineStore('partners', {
    state: () => ({
        partners: partners
    }),
    getters: {
        platinum: ({partners}): Partner[] => filterBy(partners, "platinum"),
        path: ({partners}): Partner[] => filterBy(partners, "path"),
        silver: ({partners}): Partner[] => filterBy(partners, "silver"),
        gold: ({partners}): Partner[] => filterBy(partners, "gold"),
        bronze: ({partners}): Partner[] => filterBy(partners, "bronze"),
        media: ({partners}): Partner[] => filterBy(partners, "media"),
        tech: ({partners}): Partner[] => filterBy(partners, "tech"),
        partnersMap: ({partners}): Partners => {
            return {
                platinum: filterBy(partners, "platinum"),
                path: filterBy(partners, "path"),
                gold: filterBy(partners, "gold"),
                silver: filterBy(partners, "silver"),
                bronze: filterBy(partners, "bronze"),
                media: filterBy(partners, "media"),
                tech: filterBy(partners, "tech"),
            }
        }
    },
    actions: {
        getPartnerById(id: string) {
            return this.partners.find(partner => partner.id.toLowerCase() === id.toLowerCase());
        }
    }
});

function filterBy(partners: Partner[], type: string) {
    return shuffle(partners.filter(partner => partner.type === type));
}

function shuffle<T>(array: T[]): T[] {
    return array.sort(() => 0.5 - Math.random());
}

export interface PartnersState {
    partners: Partner[];
}

export type PartnerType = 'platinum' | 'gold' | 'silver' | 'media' | 'path' | 'bronze' | 'tech';

export interface Partner {
    id: string;
    name: string;
    description: string;
    logo: string;
    www: string;
    type: PartnerType;
    orientation?: "horizontal" | "vertical" | "box";
}

export interface Partners {
    platinum: Partner[];
    path: Partner[];
    gold: Partner[];
    silver: Partner[];
    bronze: Partner[];
    tech: Partner[];
    media?: Partner[];
}

function getPartners(): Partner[] {
    return [{
        id: "join-us",
        name: "Join Us",
        description: `Join Us`,
        logo: "/assets/partners/2026/join-us.svg",
        www: "mailto:confitura@confitura.pl",
        type: "gold",
        orientation: "horizontal"
    }, {
        id: "join-us",
        name: "Join Us",
        description: `Join Us`,
        logo: "/assets/partners/2026/join-us.svg",
        www: "mailto:confitura@confitura.pl",
        type: "silver",
        orientation: "horizontal"
    }];
}