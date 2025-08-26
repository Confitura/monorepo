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

export type PartnerType = 'platinum' | 'gold' | 'silver' | 'media' | 'path' | 'bronze';

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
        name: "waiting for you",
        description: "If you're building something cool, hiring, or just want to connect with the crème de la crème of the Polish IT scene - we’re still accepting sponsors!\n" +
            "\n" +
            "We’ve got:\n" +
            "* a few sponsor spots available\n" +
            "* some workshop time slots\n" +
            "* and yes, a full-color PDF offer listing every single possibility we’ve imagined for partner visibility\n" +
            "\n" +
            "Interested? Ping Magda or the entire team at confitura [at] confitura.pl and we’ll send you the details. It’s fun, it’s creative, and it helps keep Confitura thriving and independent.",
        logo: "/assets/partners/2025/join-us.svg",
        www: "https://confitura.pl",
        type: "gold",
        orientation: "box"
    }, {
        id: "datalinks",
        name: "DataLinks",
        description: `We believe AI is only as powerful as the data it understands. That’s why we built the technology that connects unstructured and structured data into one semantic layer, giving organizations a clear, unified view of their most valuable assets.
With our approach, your data doesn’t just sit in silos, it talks. Our Entity Resolution Assistant automatically links and enriches your datasets with industry models. The Semantics Editor lets you choose and refine the connections that matter to your business. And with our Semantic Query Language, you can finally query your tabular data in a way that improves LLM accuracy and powers next-generation data apps.
Whether you’re a startup struggling with precedent problems or an enterprise juggling thousands of reports, the result is the same: cleaner, interconnected data that accelerates use cases and gives AI the structure it needs to deliver accurate, trustworthy answers - all without writing a single line of code.`,
        logo: "/assets/partners/2025/datalinks.svg",
        www: "https://datalinks.com/",
        type: "gold",
        orientation: "horizontal"
    }, {
        id: "xtb",
        name: "xtb",
        description:
            "We are a global fintech company that provides investors instant access to financial markets worldwide through an online investing platform and the XTB mobile app 📲\n" +
            "\n" +
            "Over the past two decades, we have grown our presence in the financial markets and now have more than 1,400 employees, with over 600 working in the Technology and Product Department.\n" +
            "\n" +
            "We’re a team of experts who love what we do – creating innovative solutions that make a real impact worldwide enabling our clients (over 1,7 million! ✨) to put their money to work 🚀 We take pride in our app, which boasts an impressive rating of 4.7 on the App Store. \n" +
            "\n" +
            "Our culture is built on trust, technology, and support. We work in dynamic, interdisciplinary teams where every voice is heard and valued. We embrace cutting-edge technologies to fuel our creativity and innovation, ensuring that collaboration is at the heart of everything we do. Whether you’re writing code, designing features, or solving problems, you’ll have the chance to see your ideas come to life and make a difference.\n" +
            "\n" +
            "Join us in shaping the future of finance!",
        logo: "/assets/partners/2025/XTB logo color_RGB.svg",
        www: "https://www.xtb.com/",
        type: "silver",
        orientation: "horizontal"
    }, {
        id: "join-us",
        name: "",
        description: "",
        logo: "/assets/partners/2025/reserved.svg",
        www: "/partners/join-us",
        type: "silver",
        orientation: "box"
    }, {
        id: "join-us",
        name: "",
        description: "",
        logo: "/assets/partners/2025/reserved.svg",
        www: "/partners/join-us",
        type: "silver",
        orientation: "box"
    }, {
        id: "join-us",
        name: "",
        description: "",
        logo: "/assets/partners/2025/reserved.svg",
        www: "/partners/join-us",
        type: "silver",
        orientation: "box"
    }, {
        id: "join-us",
        name: "",
        description: "",
        logo: "/assets/partners/2025/join-us.svg",
        www: "",
        type: "platinum",
        orientation: "box"
    }, {
        id: "sages",
        name: "Sages",
        description: "Działamy od 2007 r., zapewniając kompleksowe wsparcie w zakresie IT. Zajmujemy się kształceniem specjalistów IT oraz tworzeniem rozwiązań opartych na sztucznej inteligencji mających na celu automatyzację procesów w organizacjach. Wśród naszych klientów są takie firmy jak Alior Bank, OLX Group, Santander Bank Polska S.A., Orange Polska, Lufthansa i wiele innych. Wdrażamy stworzony przez Politechniką Warszawską system Omega-PSIR – oprogramowanie do ewaluacji uczelni i instytucji naukowych. Jest to nr 1 w Polsce i nr 5 na świecie, jeżeli chodzi o liczbę wdrożeń pośród oprogramowania tego typu.",
        logo: "/assets/partners/2025/sages_bdcb705c07.svg",
        www: "https://www.sages.pl/",
        type: "path",
        orientation: "horizontal"
    }, {
        id: "bottega",
        name: "Bottega",
        description: `Bottega IT Minds to społeczność ekspertów, którzy wspierają
programistów na każdym etapie kariery – od pierwszych kroków po rozwój
specjalistycznych kompetencji. Wspólnie z naszymi klientami
projektujemy racjonalne architektury, dzieląc się wiedzą,
doświadczeniem i najlepszymi praktykami.`,
        logo: "/assets/partners/2025/bottega-black.png",
        www: "https://bottega.com.pl/",
        type: "bronze",
        orientation: "horizontal"
    }, {
        id: "ai-poland",
        name: "AI Poland",
        description: `AIPoland is an initiative of Digital Poland Foundation with aim to promote Polish AI ecosystem and enable collaboration between Polish and foreign entities in various areas including business cooperation, co-development of new initiatives and raising funds.

We hope to achieve this by providing a complete overview of Polish AI scene which includes key facts, events, the database of Poland-based companies, R&D centres, Universities as well as Public Policy and support for AI development.`,
        logo: "/assets/partners/2025/aipoland_logo-2025_horizontal_rgb-kolor.svg",
        www: "https://aipoland.org/",
        type: "media",
        orientation: "horizontal"
    }, {
        id: "digitalpoland",
        name: "Digital Poland",
        description: "Fundacja Digital Poland zamienia cyfrowe wyzwania w szanse dla polskiej gospodarki. Pozycjonujemy Polskę jako wiodący ośrodek innowacji cyfrowych na świecie, promując międzynarodową i międzybranżową inicjatywę, łącząc siły, tematy i tworząc sieć kontaktów i relacji.",
        logo: "/assets/partners/2025/digital poland_logo_kolor+czarny.svg",
        www: "https://digitalpoland.org/",
        type: "media",
        orientation: "vertical"
    }, {
        id: "stacja-it",
        name: "Stacja IT",
        description: "Stacja IT to miejsce spotkań osób, dla których rozwój kompetencji i ciągłe doskonalenie umiejętności są bardzo ważne. W trakcie praktycznych szkoleń nasi Eksperci dzielą się swoją wiedzą z tymi, którzy tej wiedzy potrzebują. Nie masz czasu samodzielnie szukać materiałów, przykładów i zastanawiać się jaki warsztat pracy jest optymalny dla danej technologii? Jeśli szukałeś miejsca, w którym możesz się wiele nauczyć, świetnie trafiłeś!",
        logo: "/assets/partners/2025/stacja-it.png",
        www: "https://stacja.it/",
        type: "media",
        orientation: "vertical"
    }];
}