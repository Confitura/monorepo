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
    return [
        {
            id: "datalinks",
            name: "DataLinks",
            description: `We believe AI is only as powerful as the data it understands. That’s why we built the technology that connects unstructured and structured data into one semantic layer, giving organizations a clear, unified view of their data. With our approach, your data doesn’t just sit in silos, it talks. Our Entity Resolution Assistant automatically links and enriches your datasets with industry models. The Semantics Editor lets you choose and refine the connections that matter to your business. And with our Semantic Query Language, you can finally query your tabular data in a way that improves LLM accuracy and powers next-generation data apps. 

Whether you’re an individual developer struggling with complex problems or your enterprise is juggling thousands of reports, the result is the same: cleaner, interconnected data that accelerates use cases and gives AI the structure it needs to deliver accurate, trustworthy answers - all without writing a single line of code.`,
            logo: "/assets/partners/2025/datalinks.svg",
            www: "https://datalinks.com/",
            type: "gold",
            orientation: "horizontal"
        },
        {
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
        },
        {
            id: "sages",
            name: "Sages",
            description: "Działamy od 2007 r., zapewniając kompleksowe wsparcie w zakresie IT. Zajmujemy się kształceniem specjalistów IT oraz tworzeniem rozwiązań opartych na sztucznej inteligencji mających na celu automatyzację procesów w organizacjach. Wśród naszych klientów są takie firmy jak Alior Bank, OLX Group, Santander Bank Polska S.A., Orange Polska, Lufthansa i wiele innych. Wdrażamy stworzony przez Politechniką Warszawską system Omega-PSIR – oprogramowanie do ewaluacji uczelni i instytucji naukowych. Jest to nr 1 w Polsce i nr 5 na świecie, jeżeli chodzi o liczbę wdrożeń pośród oprogramowania tego typu.",
            logo: "/assets/partners/2025/sages_bdcb705c07.svg",
            www: "https://www.sages.pl/",
            type: "path",
            orientation: "horizontal"
        },
        {
            id: "ipipan",
            name: "Instytut Podstaw Informatyki PAN",
            description: "The Institute of Computer Science of the Polish Academy of Sciences (ICS PAS) is one of the select few leading CS research centres in Poland. According to the parametric evaluation carried out by the Polish Ministry of Science and Education, ICS PAS has been ranked among the Category A entities in Group N13 (Mathematics and Computer Science), similarly as 5 years ago and earlier. In all the directions of CS research conducted in the Institute, ICS PAS holds original results on the world level. All those directions are compliant with the program for desirable development of information technology. To put it shortly, that program coincides with the information society development program in the sense of EU Framework Programs, as well as the UE and national strategic programs.",
            logo: "/assets/partners/2025/ipi.svg",
            www: "https://ipipan.waw.pl/",
            type: "path",
            orientation: "box"
        },
        {
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
        },
        {
            id: "ai-poland",
            name: "AI Poland",
            description: `AIPoland is an initiative of Digital Poland Foundation with aim to promote Polish AI ecosystem and enable collaboration between Polish and foreign entities in various areas including business cooperation, co-development of new initiatives and raising funds.

We hope to achieve this by providing a complete overview of Polish AI scene which includes key facts, events, the database of Poland-based companies, R&D centres, Universities as well as Public Policy and support for AI development.`,
            logo: "/assets/partners/2025/aipoland_logo-2025_horizontal_rgb-kolor.svg",
            www: "https://aipoland.org/",
            type: "media",
            orientation: "horizontal"
        },
        {
            id: "digitalpoland",
            name: "Digital Poland",
            description: "Fundacja Digital Poland zamienia cyfrowe wyzwania w szanse dla polskiej gospodarki. Pozycjonujemy Polskę jako wiodący ośrodek innowacji cyfrowych na świecie, promując międzynarodową i międzybranżową inicjatywę, łącząc siły, tematy i tworząc sieć kontaktów i relacji.",
            logo: "/assets/partners/2025/digital poland_logo_kolor+czarny.svg",
            www: "https://digitalpoland.org/",
            type: "media",
            orientation: "vertical"
        },
        {
            id: "stacja-it",
            name: "Stacja IT",
            description: "Stacja IT to miejsce spotkań osób, dla których rozwój kompetencji i ciągłe doskonalenie umiejętności są bardzo ważne. W trakcie praktycznych szkoleń nasi Eksperci dzielą się swoją wiedzą z tymi, którzy tej wiedzy potrzebują. Nie masz czasu samodzielnie szukać materiałów, przykładów i zastanawiać się jaki warsztat pracy jest optymalny dla danej technologii? Jeśli szukałeś miejsca, w którym możesz się wiele nauczyć, świetnie trafiłeś!",
            logo: "/assets/partners/2025/stacja-it.png",
            www: "https://stacja.it/",
            type: "media",
            orientation: "vertical"
        },
        {
            id: "match-trade-technologies",
            name: "Match-Trade Technologies",
            description: "\n" +
                "Jako firma z branży fintech opracowujemy zaawansowane systemy transakcyjne dla rynku Forex, kontraktów CFD, kryptowalut oraz akcji. Nasz flagowy produkt - Match-Trader to platforma tradingowa wykorzystywana przez traderów detalicznych oraz instytucjonalnych do handlu i dostarczania płynności. Aplikacja oparta jest o nasz autorski matching engine wykonujący egzekucję zleceń w czasie poniżej kilku milisekund, który wykorzystywany jest również przez inne firmy technologiczne do tworzenia własnych platform. Drugim większym rozwiązaniem stworzonym przez naszych programistów jest Match2Pay - bramka płatnicza umożliwiająca wpłaty i wypłaty w kryptowalutach, wspierająca najważniejsze blockchainy takie jak m.in. Bitcoin, Tron czy Ethereum. Z naszych rozwiązań korzysta ponad 150 firm z całego świata, głównie brokerów Forexowych, ale rozwijamy się również na rynku iGamingu czy Prop Tradingu.\n" +
                "\n" +
                "Ponadto w ramach jednego z naszych głównych projektów, zajmujemy się także budową algorytmów z zakresu High Frequency Tradingu. Algorytmy te każdego dnia obracają kryptowalutami wartymi miliony dolarów. Nasze aplikacje, w których każda milisekunda ma kluczowe znaczenie, przetwarzają gigabajty danych aby podejmować optymalne decyzje związane z zawieranymi transakcjami.  Tworzone przez nas algorytmy są kluczowym na rynku kryptowalut dostawcą płynności (market makerem), a głównym ich zadaniem jest wzbogacenie rynku o płynność, potrzebną aby klienci giełd kryptowalutowych mogli sprzedawać lub kupować waluty po jak najlepszych cenach. Nasze rozwiązania są wykorzystywane na giełdach z aż pięciu kontynentów. Każdego miesiąca staramy się zwiększać nasz udział w tym niezwykle konkurencyjnym rynku, wśród rywalizujących ze sobą market makerów. Jesteśmy aktywnym uczestnikiem DEXów (Decentralized Exchange) oraz innych rozwiązań dziedziny z rozproszonych finansów (DeFi). Członkowie naszego zespołu, nierzadko pasjonaci technologii blockchain oraz kryptowalut, każdego dnia zapoznają się z nowinkami tej nowej, dynamicznie rozwijającej się części sektora finansowego.\n" +
                "\n" +
                "Staramy się tworzyć jak najlepsze warunki do rozwoju wiedzy, umiejętności i własnych pomysłów. Szeroki zakres usług oferowanych przez Match-Trade Technologies stwarza dla naszych pracowników możliwości uczestniczenia w różnorodnych projektach, nie tylko dzięki wykorzystaniu różnych technologii, ale także dzięki pracy z biznesami z całego świata.\n" +
                "\n" +
                "W MTT do każdego pracownika podchodzimy bardzo indywidualnie a ścieżka kariery różni się w zależności od doświadczenia Kandydata oraz zespołu. Może to być zwiększenie odpowiedzialności i samodzielności lub dołączanie a z czasem prowadzenie coraz bardziej złożonych projektów.",
            logo: "/assets/partners/2025/match-trade.png",
            www: "https://match-trade.com/",
            type: "silver",
            orientation: "horizontal"
        },
        {
            id: "it-wiz",
            name: "ITwiz",
            description: "ITwiz powstał po to, aby dostarczyć jak najlepszych narzędzi do komunikacji z osobami podejmującymi decyzje o zakupie rozwiązań informatycznych. ITwiz to miesięcznik dostępny w wersji drukowanej i cyfrowej, serwis internetowy, komunikacja przez social media, a także spotkania networkingowe i konferencje prowadzone przez najlepszych ekspertów specjalizujących się w nowoczesnych technologiach.",
            logo: "/assets/partners/2025/itwiz logo_czarne.png",
            www: "https://itwiz.pl",
            type: "media",
            orientation: "box"
        },
        {
            id: "kodolamacz",
            name: "Kodołamacz",
            description: "Specjalizujemy się w rozwoju kompetencji w obszarze sztucznej inteligencji i nowoczesnych technologii IT. Oferujemy intensywne kursy rozwojowe oraz kompleksowe ścieżki edukacyjne, takie jak AI & Data Science czy Machine Learning Engineering, które przygotowują do najbardziej poszukiwanych zawodów na rynku. Dzięki ofercie kursów w formule blended learning uczestnicy łączą elastyczną naukę online z praktycznymi warsztatami na żywo, a szeroka oferta szkoleń wieczorowych i weekendowych z AI pozwala rozwijać się w tempie dopasowanym do indywidualnych potrzeb. Stawiamy na praktykę i rozwój w AI, wspierając zarówno osoby przebranżawiające się, jak i specjalistów, którzy chcą wejść na wyższy poziom pracy z nowymi technologiami.",
            logo: "/assets/partners/2025/kodolamacz_dark_sages.svg",
            www: "https://www.kodolamacz.pl",
            type: "media",
            orientation: "horizontal"
        },
        {
            id: "revolut",
            name: "Revolut",
            description: "",
            logo: "/assets/partners/2025/Revolut.svg",
            www: "https://www.revolut.com/",
            type: "gold",
            orientation: "horizontal"
        }, {
            id: "inpost",
            name: "InPost",
            description: "InPost has revolutionised e-commerce parcel delivery in Poland and is now __one of Europe’s leading OOH e-commerce enablement platforms__. Founded in 1999 by Rafał Brzoska, InPost provides delivery services through our network of over 50,000 Automated Parcel Machines (APMs) and almost 35,000 pick-up drop-off points (PUDO) in nine countries across Europe, as well as to-door courier and fulfilment services to e-commerce merchants. InPost’s lockers provide consumers with a cheaper and more flexible, convenient, environmentally friendly and contactless delivery option.\n",
            logo: "/assets/partners/2025/inpost.png",
            www: "https://inpost.pl/",
            type: "gold",
            orientation: "horizontal"
        },
        {
            id: "decerto",
            name: "Decerto",
            description: "Decerto od 19 lat realizuje projekty informatyczne, które zmieniają oblicze branży ubezpieczeniowej – w Polsce, Wielkiej Brytanii oraz w USA. Obecnie 80% rodzimego rynku insurance obsługiwane jest przez Decerto. Do naszych flagowych produktów należą: Agent Portal (kompleksowe narzędzie dla agentów ubezpieczeniowych) oraz autorskie rozwiązanie Higson (Business Rules Engine).\n" +
                "\n" +
                " \n" +
                "\n" +
                "W 2024 r. firma otrzymała certyfikat Great Place to Work, z imponującym wynikiem ankiet, w których 91% pracowników oceniło Decerto jako doskonałe miejsce pracy. Zadowolenie pracowników odzwierciedla nieustanne wysiłki firmy na rzecz tworzenia kultury pracy, która promuje szacunek, współpracę oraz ciągłe doskonalenie.\n" +
                "\n" +
                " \n" +
                "\n" +
                "Po więcej zapraszamy na naszą stronę: [https://www.decerto.com/](https://www.decerto.com/)",
            logo: "/assets/partners/2025/decerto.png",
            www: "https://www.decerto.com/",
            type: "gold",
            orientation: "horizontal"
        },
        {
            id: "netcompany",
            name: "Netcompany",
            description: "Netcompany is an international IT Consultancy with 8,000+ employees spread across 10+ countries, with its headquarters located in Copenhagen, Denmark. We deliver business-critical strategic IT projects that accelerate the digital transformation, with customers from a wide array of industries, including the public and private sectors. Our goal is to empower societies and citizens of Europe to stand independently and strongly, and to lead the way in shaping a better future for all.",
            logo: "/assets/partners/2025/netcompany.png",
            www: "https://netcompany.com/",
            type: "gold",
            orientation: "horizontal"
        },
        {
            id: "starburst",
            name: "Starburst",
            description: "Starburst is the flexible data platform that delivers fast, secure access to all your data wherever it lives. Built on an open data stack with Trino and Apache Iceberg, Starburst unifies distributed data, across clouds, on-premises, and in hybrid environments, without complex or costly migrations, unleashing the full power of the data lakehouse for analytics and AI.\n\nWith our Lakeside AI architecture, organizations gain federated access, governed collaboration, and full data lineage, empowering compliant, scalable AI. Trusted by global leaders, Starburst helps enterprises turn data into business value.\n\nFrom insights to action to AI, Starburst fuels innovation at every level. Learn more at [https://starburst.ai](https://starburst.ai)",
            logo: "/assets/partners/2025/starburst-dark.jpg",
            www: "https://www.starburst.io/",
            type: "bronze",
            orientation: "horizontal"
        },
        {
            id: "best-warsaw",
            name: "BEST Warsaw",
            description: "",
            logo: "/assets/partners/2025/BEST_warsaw.png",
            www: "https://www.facebook.com/BEST.WARSAW/",
            type: "media",
            orientation: "horizontal"
        }];
}