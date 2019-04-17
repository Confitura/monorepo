import { Module } from 'vuex';
import { LOAD_PARTNER_BY_ID, LOAD_PARTNERS, Partner, RootState } from '@/types';

export const partnersModule: Module<PartnersState, RootState> = {
  state: {
    partners: [],
  },
  getters: {
    platinum: ({ partners }): Partner[] => partners.filter((partner) => partner.type === 'platinum'),
    silver: ({ partners }): Partner[] => shuffle(partners.filter((partner) => partner.type === 'silver')),
    gold: ({ partners }): Partner[] => shuffle(partners.filter((partner) => partner.type === 'gold')),
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
          logo: require('../assets/partners/softwareplant.svg'),
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
          logo: require('../assets/partners/7N.svg'),
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
          logo: require('../assets/partners/volvo.svg'),
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
          logo: require('../assets/partners/dynatrace.svg'),
        }, {
          id: 'eworkgroup',
          description: `Ework Group jest wiodącą siecią niezależnych konsultantów i liderem na rynku skandynawskim. Działamy w sektorach IT, telekomunikacji, nowych technologii oraz rozwoju biznesu. Współpracujemy z ponad 9000 konsultantów świadczących usługi dla ponad 170 klientów. 

Akcje Ework są notowane na giełdzie NASDAQ OMX w Sztokholmie. W Polsce istniejemy od 2015 roku przekształcając oblicze rodzimej branży IT.`,
          name: 'Ework group',
          www: 'https://www.eworkgroup.com/pl',
          type: 'silver',
          logo: require('../assets/partners/ework_group.svg'),
          orientation: 'horizontal',
        }, {
          id: 'nfj',
          description: `[No Fluff Jobs](https://nofluffjobs.com/) to najpopularniejszy portal z ogłoszeniami IT w Polsce. U nas każde ogłoszenie jest aktualne, zawiera widełki płacowe, a także wszystkie szczegóły na temat danego stanowiska, przedstawione w prosty i czytelny sposób. Już od 3 lat zmieniamy standardy rekrutacji IT w naszym kraju, przekonując pracodawców do transparentności i uczciwości wobec potencjalnych pracowników.`,
          name: 'No Fluff Jobs',
          www: 'https://www.nofluffjobs.com ',
          type: 'silver',
          logo: require('../assets/partners/nfj.svg'),
          orientation: 'horizontal',
        }, {
          id: 'viacom',
          description: `As a part of leading global entertainment company Viacom.tech offers challenging projects for iconic brands like MTV, Nickelodeon, Nick Jr, COMEDY CENTRAL, and Paramount Channel. Focusing on Viacom’s global engineering organization, the Warsaw Software Development Center plays a crucial role in the creation of highly efficient, advanced systems which are implemented by millions of users around the world. Our excellence center in Warsaw builds scalable and efficient systems, serving high volume traffic. We are an Agile and growing team of software engineers who use modern development and deployment technologies (Java 11, Kotlin, Swift, TypeScript, Docker, Kubernetes, clouds).
 
Viacom.Tech is pioneering a new era of data in entertainment with opportunities in mobile application development, artificial intelligence, and data science. Want to become an architect of change and join a team of innovative thought leaders who aim to transform the entertainment industry with technological expertise? Here you can check out our current job openings and learn more about the company: https://viacom.tech/`,
          name: 'Viacom.Tech',
          www: 'https://viacom.tech/',
          type: 'gold',
          logo: require('../assets/partners/viacom.svg'),
          orientation: 'horizontal',
        }, {
          id: 'bosch',
          description: `W Warszawskich oddziale Robert Bosch Sp. z. o.o. zatrudnionych jest prawie 300 specjalistów IT. Zajmują się oni konsultacjami, projektowaniem, zarządzaniem i rozwojem oprogramowania. Około połowa z nich to testerzy lub programiści języków .Net, Java, JS, J2EE, ABAP i baz danych Oracle. W wyniku ich pracy powstaje oprogramowanie, które wspiera Bosch Group na całym świecie w zakresie logistyki, IT Security, produkcji oraz e-commerce.

Dołącz do nas jeśli:

* Zależy ci na jakości Twojego kodu
* Cenisz sobie samodzielność i autonomię w działaniu
* Chcesz mieć wpływ na projekt, w którym uczestniczysz
* Znasz angielski i chcesz pracować w wielokulturowym środowisku


Zobacz, jak się u nas pracuje: [https://www.youtube.com/watch?v=A2YWrsEDLIg](https://www.youtube.com/watch?v=A2YWrsEDLIg)`,
          name: 'Bosch',
          www: 'https://www.bosch-career.pl/IT',
          type: 'silver',
          logo: require('../assets/partners/bosch.svg'),
          orientation: 'horizontal',
        }, {
          id: 'allegro',
          name: 'Allegro Tech',
          description: `W [Allegro](https://allegro.tech/) tworzymy i utrzymujemy aplikacje, które dzięki swojej skalowalności i niezawodności zyskały sobie rzeszę wiernych użytkowników w całej Europie Środkowo-Wschodniej. Zadanie nie należy do najłatwiejszych, w pracy napotykamy na szereg wyzwań zarówno w obszarze samej architektury i designu, jak i w procesie doboru technologii, zapewnienia jakości kodu, usprawnienia funkcjonalności oraz w późniejszej fazie wdrożenia i utrzymania produktu.  
allegro.tech jest naszym pomysłem na dzielenie się zebranym doświadczeniem poprzez organizację konferencji, warsztatów, meetupów i hackatonów.
          
Po więcej informacji zapraszamy na nasz blog [https://allegro.tech/](https://allegro.tech/)
          
Dołacz do nas również na [https://www.meetup.com/allegrotech/](https://www.meetup.com/allegrotech/)
          
I bądź na bieżąco [https://www.facebook.com/allegro.tech](https://www.facebook.com/allegro.tech)`,
          www: 'https://allegro.tech/',
          type: 'silver',
          logo: require('../assets/partners/allegro.svg'),
          orientation: 'horizontal',
        }, {
          id: 'applause',
          name: 'Applause',
          description: `Applause is the worldwide leader in crowdtesting and digital quality. Software is at the heart of how all brands engage users, and digital experiences must work flawlessly everywhere. With highly-vetted testers available on-demand around the globe, Applause provides brands with a full suite of testing and feedback capabilities. This approach drastically improves testing coverage, eliminates the limitations of offshoring and traditional QA labs, and speeds time-to-market for websites, mobile apps, IoT, and in-store experiences.  
Thousands of leading companies — including Ford, Fox, Google, and Dow Jones — rely on Applause as a best practice to deliver high-quality digital experiences that customers love.`,
          www: 'https://www.applause.com/join-our-team',
          type: 'silver',
          logo: require('../assets/partners/applause.svg'),
          orientation: 'horizontal',
        },{
          id: 'nordea',
          name: 'Nordea',
          description: `Nordea w Polsce jest częścią największej skandynawskiej instytucji finansowej. Funkcjonuje jako centrum operacyjne w Łodzi oraz IT w Trójmieście i Warszawie. IT w Polsce dostarcza kompleksowe rozwiązania w zakresie utrzymania i rozwoju platform i systemów informatycznych oraz uczestniczy we wdrożeniu jednego z największych systemów bankowych na świecie. Nordea planuje wzrost zatrudnienia. Poszukuje absolwentów studiów wyższych i ekspertów o ugruntowanej wiedzy informatycznej.`,
          www: 'https://www.nordea.pl',
          type: 'silver',
          logo: require('../assets/partners/nordea.svg'),
          orientation: 'horizontal',
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

function shuffle<T>(array: T[]): T[] {
  return array.sort(() => 0.5 - Math.random());

}

export interface PartnersState {
  partners: Partner[];
}
