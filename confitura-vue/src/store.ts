import Vue from 'vue';
import Vuex, { StoreOptions } from 'vuex';
import { CHANGE_HEADER_THEME, LOAD_PARTNER_BY_ID, LOAD_PARTNERS, Partner, RootState, WINDOW_RESIZED } from '@/types';

Vue.use(Vuex);
const storeOptions: StoreOptions<RootState> = {
  state: {
    headerTheme: 'default',
    headerHeight: 73,
    windowWidth: 0,
    date: '2019-06-29T09:00',
    partners: [],
  },
  getters: {
    isSm: (state) => state.windowWidth >= 576,
    isMd: (state) => state.windowWidth >= 768,
    isLg: (state) => state.windowWidth >= 992,
    isXl: (state) => state.windowWidth >= 1200,
    platinum: (state): Partner[] => state.partners.filter((partner) => partner.type === 'platinum'),
    silver: (state): Partner[] => state.partners.filter((partner) => partner.type === 'silver'),

  },
  mutations: {
    [CHANGE_HEADER_THEME](store, theme: { color: string }) {
      store.headerTheme = theme.color;
    },

    [WINDOW_RESIZED](store, size: { width: number }) {
      store.windowWidth = size.width;
      if (size.width >= 992) {
        store.headerHeight = 73;
      } else {
        store.headerHeight = 60;
      }
    },
  },
  actions: {
    [LOAD_PARTNERS]({ state }) {
      // tslint:disable
      state.partners = [
        {
          id: 'softwareplant',
          description: `Technologiczny startup rozwijający produkty do nowoczesnego zarządzania projektami
          z bazą 3mln użytkowników i ponad 8000 klientów. Wśród obsługiwanych firm znajdują się m.in.
          Tesla, Intel, Oracle, Bloomberg, Sony, a także wiele instytucji rządowych, uniwersytetów
          i organizacji społecznych.\n\nFirma przywiązuje ogromną uwagę do jakości tworzonego
          oprogramowania. Kod pisany jest zgodnie z zasadami Domain Driven Design z typową dla
          tego podejścia architekturą heksagonalną. Dekompozycja na domeny generyczne, główną
          i pomocnicze umożliwia sprawny rozwój nowych funkcjonalności w stale rosnącym zespole
          i kodzie źródłowym, który liczy już ponad 0,7mln linii kodu. Kod domenowy jest wolny
          od zależności do środowiska, baz danych, czy modeli ORM, które dostarczane są w warstwie
          infrastrukturalnej. Frontend oparty jest na Angular5 Typescript z Webpackiem,
          gdzie także gości DDD.\n\nKażda zmiana w kodzie podlega złożonemu procesowi,
          którego pierwszym krokiem jest automaczne review angażujące kilkadziesiąt kontenerów
          dockerowych, analizatory statyczne, testy jednostkowe oraz integracyjne. Kolejnym krokiem
          jest peer review dbające o propagacje wiedzy i wspólne zrozumienie domeny. Końcowe kroki
          to rozbudowane pipeline-y tworzące continuous delivery, które pozwalają dostarczać nowe
           wersje oprogramowania w bardzo krótkich cyklach.\n\nOstatni rok dla SoftwarePlant to
            dynamiczny rozwój klientów w modelu SaaS, dla których firma wykorzystuje farmę serwerów
             AWS wraz z Elasticsearch, Influx oraz Grafana do centralnego monitoringu całości
              rozwiązania.\n\nOdwiedź nas na stoisku podczas Confitury lub w
               [naszym biurze](https://www.youtube.com/watch?v=xNJ-el6bqyg).`,
          name: 'SoftwarePlant',
          www: 'https://softwareplant.com/',
          type: 'platinum',
          logo: require('./assets/partners/softwareplant.svg'),
        }, {
          id: '7n',
          description: `[7N](https://www.7n.com/) to duńska firma konsultingowa, zajmująca się
          outsourcingiem ekspertów IT.
          Globalnie, w ośmiu krajach zatrudniamy ponad 1000 osób (w Polsce ponad 600).
          W ciągu prawie 30 lat działalności dowiedliśmy, że jawny i przejrzysty model finansowy,
          współpraca wyłącznie z ekspertami w swoich dziedzinach oraz bardzo dobra opieka nad nimi są
          najlepszym modelem konsultingu IT.\n\nJesteśmy indywidualnym agentem naszych konsultantów.
          Promujemy ich kompetencje u naszych klientów, oferując możliwość uczestniczenia w
          różnych projektach, dołączając do tego finansową przejrzystość, wsparcie w rozwoju
          zawodowym i zawodową stabilność. Naszym głównym założeniem jest długoterminowość współpracy.
          Duża część zespołu 7N to osoby pracujące dla nas od wielu lat.`,
          name: '7n',
          www: 'https://7n.com/',
          type: 'silver',
          logo: require('./assets/partners/7N.svg'),
        }, {
          id: 'volvo',
          description: `Volvo Group IT we Wrocławiu jest jednym z dwóch (obok Bangalore w Indiach)
          Globalnych Centrów IT w Grupie Volvo i odpowiada za tworzenie, rozwój i utrzymanie rozwiązań
          informatycznych dla koncernu Volvo. Przez lata zbudowaliśmy silny zespół ekspertów IT z zakresu
          architektury systemów IT, analizy biznesowej, programowania aplikacji, zarządzania projektami i
          testami. Ważną częścią Volvo Group IT we Wrocławiu jest dział metod i narzędzi, który opracowuje
          metodyki i modele stosowane w usługach dostarczanych w oparciu o światowe standardy, np. Devops,
          ITIL, Agile, Lean oraz o rozwiązania takie, jak Software Defect Prediction, które wyróżnia Volvo
          na rynku IT.\n\nDołącz do nas: [www.volvogroup.pl/kariera](www.volvogroup.pl/kariera)`,
          name: 'Volvo Group',
          www: 'https://www.volvogroup.pl/kariera',
          type: 'silver',
          logo: require('./assets/partners/volvo.svg'),
        }, {
          id: 'dynatrace',
          description: `**WHO WE ARE?**

We thrive in a culture of innovation, with bytes running through our veins and code filling our brains. We deliver smart solutions that beat market standards by light years.

For 8 years running, we have been and continue to be the leaders in the Application Performance Market because we create and develop an intelligent solution that enables thousands of software companies to deliver perfect user experience.

“The software you build is only as good as the people who make it, we made a concerted effort that we must attract and retain the best developers in the world. It helps that our culture is unique, and our people feel as though they are part of creating something that isn’t evolutionary – it’s revolutionary.”

John Van Siclen  
Chief Executive Officer

**WHAT WE DO?**

We believe that thanks to us, the world changes for better, as we enable the great software people use daily around the world to work perfectly.

At Dynatrace Gdansk Lab, we design, create and develop a best-in-class product that provides full insight into the performance of software products and IT systems. We continuously improve and deliver a solution that enables companies to scale up and make bold decisions concerning their future. Our Dynatrace solution monitors the full stack –from the network, through the database and application tiers, into the code-specific analysis, and all the way out to end-user devices and third-party add-ins. The same holds true for in-house hosted applications and enterprise clouds, public clouds, and hybrid clouds where containerization, hyper-scale, and elastic computing dominate. All of this is automated, not only the initial setup and instrumentation, but also problem identification, dashboards, ongoing adaptation to environmental and application changes, and upgrading the Dynatrace platform itself. We create Dynatrace because the world needs software to work perfectly.`,
          name: 'Dynatrace',
          www: 'https://jobs.dynatrace.pl/',
          type: 'silver',
          logo: require('./assets/partners/dynatrace.svg'),
        },
      ];
    },
    // tslint:enable
    [LOAD_PARTNER_BY_ID]({ state, dispatch }, id: string) {
      return dispatch(LOAD_PARTNERS)
        .then(() => state.partners.find((partner) => partner.id.toLowerCase() === id.toLowerCase()));
    },
  },
};
export default new Vuex.Store(storeOptions);

