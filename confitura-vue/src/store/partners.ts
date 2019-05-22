import { Module } from 'vuex';
import { LOAD_PARTNER_BY_ID, LOAD_PARTNERS, Partner, RootState } from '@/types';

export const partnersModule: Module<PartnersState, RootState> = {
  state: {
    partners: [],
  },
  getters: {
    platinum: ({ partners }): Partner[] => shuffle(partners.filter((partner) => partner.type === 'platinum')),
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
          id: 'ey',
          description: `EY Global Delivery Services (GDS) is a network of EY competence centers located in five countries: India, China, Poland, Argentina, and the Philippines. [EY GDS Poland](www.workatgds.pl) provides high quality and value-added support to our customers who are EY firms and other companies, working to agreed principles of exceptional client service. The technology departments of EY GDS Poland include teams working for external clients from around the world, as well as specialists who create and develop internal EY apps.`,
          name: 'EY',
          www: 'https://workatgds.pl/pl/',
          type: 'platinum',
          logo: require('../assets/partners/ey.svg'),
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
        }, {
          id: 'nordea',
          name: 'Nordea',
          description: `Nordea w Polsce jest częścią największej skandynawskiej instytucji finansowej. Funkcjonuje jako centrum operacyjne w Łodzi oraz IT w Trójmieście i Warszawie. IT w Polsce dostarcza kompleksowe rozwiązania w zakresie utrzymania i rozwoju platform i systemów informatycznych oraz uczestniczy we wdrożeniu jednego z największych systemów bankowych na świecie. Nordea planuje wzrost zatrudnienia. Poszukuje absolwentów studiów wyższych i ekspertów o ugruntowanej wiedzy informatycznej.`,
          www: 'https://www.nordea.pl',
          type: 'silver',
          logo: require('../assets/partners/nordea.svg'),
          orientation: 'horizontal',
        }, {
          id: 'britenet',
          name: 'Britenet',
          description: `Britenet to software house realizujący projekty dla międzynarodowych klientów. Firma działa na rynku od 13 lat, zatrudnia obecnie ponad 500 pracowników i ma biura w Warszawie, Poznaniu, Lublinie, Kielcach oraz Białymstoku.  
Rozwój zawodowy idzie w parze z dobrą atmosferą, o czym świadczy tytuł Najlepsze Miejsce Pracy IT w Polsce zdobyty przez firmę aż cztery razy w badaniach Computerworld - TNS.`,
          www: 'https://britenet.com.pl',
          type: 'silver',
          logo: require('../assets/partners/britenet.svg'),
          orientation: 'horizontal',
        }, {
          id: 'kmd',
          name: 'KMD',
          description: `For more than 40 years, KMD has played a key role in digitizing the Danish state, helping make Denmark’s public sector one of the best run, most efficient and highly digitized in the world. KMD is a leading supplier of mission-critical software, solutions and IT services primarily to Denmark’s public sector, but with a growing footprint in the private sector both domestically and in Sweden and Norway. We have a broad portfolio of products and services, based predominantly on our own software solutions.

We’ve been rapidly growing and we have more than 420 IT and business specialists in our Warsaw office. They translate complex processes and legislation into simple and effective IT solutions. KMD Poland is the largest foreign affiliated branch at KMD Group. Our mission is to provide new technologies and solutions as well as to maintain and develop our systems. We help Scandinavian business and public sector make another major leap in their digital evolution.`,
          www: 'http://careers.kmdpoland.pl',
          type: 'silver',
          logo: require('../assets/partners/kmd.svg'),
          orientation: 'horizontal',
        }, {
          id: 'j-labs',
          name: 'j-labs',
          description: `We are polish software company founded by engineers for engineers in 2008 with all the best business practices and values.  
We provide complete development teams, individual engineers and outsourcing of whole project in agile models in the field of software development.  
Over 250 engineers work in our branches in Krakow and Warsaw. We support companies in the various sectors - such as banking, aviation, telco, as well as niches like machine learning, woodwork, and IoT.  
Our activities based on technology, content, and reliability have been recognized by the Forbes Diamond 2017, and the distinction in the Business Gazelle 2018 ranking.
 
Join us:  [https://www.j-labs.pl/open-positions/](https://www.j-labs.pl/open-positions/)`,
          www: 'https://www.j-labs.pl',
          type: 'silver',
          logo: require('../assets/partners/jlabs.svg'),
          orientation: 'horizontal',
        }, {
          id: 'decerto',
          name: 'decerto',
          description: `#### W skrócie o nas:

Decerto to polska firma informatyczna, specjalizująca się w budowie i wdrażaniu systemów dla biznesu. Na wysoką pozycję wśród dostawców IT składa się 12 letnie doświadczenie we współpracy z wiodącymi firmami na rynkach ubezpieczeniowym, finansowym i medialnym oraz wysoko wykwalifikowana kadra. 
Posiadamy autorskie rozwiązania oraz tworzymy systemy dostosowane do indywidualnych potrzeb Klientów. Preferujemy długą i stabilną współpracę opartą na spełnianiu oczekiwań, zaufaniu i odpowiedniej kulturze pracy.

#### Kultura  pracy:

Największy nacisk kładziemy na odpowiednie kwalifikacje swoich współpracowników oraz ich satysfakcję z pracy. Poprzez pracę z najlepszymi oraz pozytywną atmosferę stwarzamy warunki do kreatywnej pracy i osiągania sukcesów.
Nasz zespół liczy ok. 140 osób. Stawiamy na przyjazną atmosferę, wzajemny szacunek i pomoc. Cechuje nas nastawienie na cel, aktywne szukanie rozwiązań i elastyczne podejście do zagadnień.
Praca to znacząca część naszego życia, dlatego dbamy o to, by w firmie wszyscy czuli się komfortowo. Lubimy też spotkać się po godzinach pracy ☺

#### Nasza oferta:

- Możliwość wyboru przydziału projektowego – różnorodne projekty, różne lokalizacje
- Wewnętrzne szkolenia oraz TechFlashe
- Dodatkowy, roczny budżet na rozwój
- Dofinansowanie certyfikatów
- Dowolny model współpracy
- Pakiet benefitów 
- Wyjazdy i spotkania integracyjne

Stawiamy na wzajemną uczciwość, otwartość i zaufanie. Jesteśmy elastyczni wobec potrzeb naszych współpracowników i tego samego oczekujemy. 

Poznaj nas i dołącz do zespołu: 

[www.decerto.pl/kariera](http://www.decerto.pl/kariera)  
[www.facebook.com/FirmaDecerto](https://www.facebook.com/FirmaDecerto )  
[www.instagram.com/firmadecerto/](http://www.instagram.com/firmadecerto/)
`,
          www: 'https://decerto.pl/kariera.html',
          type: 'silver',
          logo: require('../assets/partners/decerto.svg'),
          orientation: 'horizontal',
        }, {
          id: 'roche',
          name: 'roche',
          description: `Founded in Switzerland in 1896, [Roche](https://it.roche.pl/) has helped millions of patients around the world with innovative pharmaceuticals and diagnostics. We have been at the forefront of cancer research and treatment for over 50 years. We invest around 9 billion Swiss francs in R&D every year because innovation is our lifeblood. We need advanced IT solutions to perform research, development, production and distribution of new medicines. 

Roche Global IT Solution Centre is based in Poland, in two locations: Warsaw and Poznan, and uses the cutting-edge technologies to improve healthcare. We are more than 500 people who create solutions that help doctors, patients and scientist around the world.`,
          www: 'https://it.roche.pl',
          type: 'gold',
          logo: require('../assets/partners/roche.svg'),

        }, {
          id: '4finance',
          name: '4finance',
          description: `
We are one of Europe’s largest digital consumer lending groups. We get money to people when they need it – quickly, conveniently and responsibly.  
We use cutting edge technology and data to offer fast and convenient loans to customers across 16 countries.  

Since we were established in 2008, we have provided more than 15,000,000 Single Payment, Line of Credit and Instalment Loans totalling over EUR 6.0 billion.

4finance IT is part of 4finance Group. We focus on bringing the right mix of technologies to deliver a great service for our customers, making sure our customers receive a simple and fast service. Our IT teams work across Europe to develop global products and support local initiatives with skill and insight.
 
[https://www.4financeit.com/](https://www.4financeit.com/)  
[https://www.4finance.com](https://www.4finance.com)  
[https://www.4finance.com/jobs](https://www.4finance.com/jobs)  `,
          www: 'https://www.4financeit.com/',
          type: 'silver',
          orientation: 'horizontal',
          logo: require('../assets/partners/4finance.svg'),

        }, {
          id: 'evojam',
          name: 'evojam',
          description: `Evojam is an unique blend of flexibility & quality. Strong technology with lightweight approach. Small team who can deliver difficult projects.  
Our motto is KEEP GROWING. That's why Evojam's core values are learning and doing good quality work. This means we use every opportunity to learn, and we know how important teamwork is in growing and learning.  
We build applications that work, that scale, that can be maintained over a longer time. We use Scrum and modern tech-stack: Scala, Java8+, NodeJS, Angular, TypeScript, React, MongoDB and plenty more.  
Our primary customers are startups in the scaling phase from around the globe.

For growth, fun & building stuff that works, join Evojam!`,
          www: 'https://www.evojam.com',
          type: 'silver',
          orientation: 'horizontal',
          logo: require('../assets/partners/evojam.svg'),

        },{
          id: 'nbc',
          name: 'nbc',
          description: `Robimy projekty. Budujemy zespoły.  
Tworzenie oprogramowania i ciągłe szukanie innowacyjnych rozwiązań to coś co nas nakręca i motywuje do działania.   
Pomagamy klientom z branży finansów, bankowości, mediów i nowych technologii poprzez m.in. rozbudowę zespołów deweloperskich, utrzymanie oprogramowania czy też rozwój systemów.

Co nas cechuje?  
* pracujemy z grupą najlepszych programistów, projektantów i specjalistów IT w Polsce. 
* dbamy o to by szybko i rzetelnie dobierać osoby odpowiedzialne za projekt IT 
* łączymy pasję z profesjonalizmem. 
* w doborze i selekcji pracowników kierujemy się doświadczeniem. Aż 40 % naszego zespołu stanowią eksperci, kolejne 45% to wysoko wykwalifikowani specjaliści. 
* realizujemy innowacyjne i różnorodne projekty. Uwzględniając wszystkie preferencje oferujemy elastyczną formę zatrudnienia i wybór świadczeń. Prace zdalną w naszym biurze lub bezpośrednio u klienta.

Do współpracy zapraszamy:   
* programistów, 
* analityków, 
* administratorów, 
* projektantów, 
* kierowników projektów, 
* konsultantów, 
* testerów oraz IT managerów

Satysfakcja ludzi, z którymi pracujemy, jest dla nas kluczowa. Dlatego indywidualnie podchodzimy zarówno do swoich partnerów biznesowych, jak i do naszych pracowników. Chcemy zrozumieć Twoje cele oraz plany i pomóc Ci w ich osiągnięciu.   
Jesteśmy w całości samofinansującą się polską firmą, która wszystkie swoje działania opiera na konkretnych wartościach przyświecających nam od początku istnienia. Dzięki konsekwencji i niesamowitemu zespołowi zdobyliśmy uznanie w wielu prestiżowych rankingach, takich jak: Gazele Biznesu, ComputerWorld Top200 i IT@Bank.   
Adresy naszych biur:
* Wrocław - siedziba główna NBC - ul. Grabiszyńska 251a. 
* Warszawa - Chmielna Business Center, ul. Chmielna 132/ 134. 
* Gdańsk - Tryton Business House, ul. Jana z Kolna 11
* Kraków - ul. Basztowa 3/16. 
* Katowice - ul. Zabrska 16/3 

Zapraszamy na naszą stronę: [www.nbc.com.pl](https://www.nbc.com.pl)`,
          www: 'https://www.nbc.com.pl',
          type: 'silver',
          logo: require('../assets/partners/nbc.svg'),

        }, {
          id: 'alior',
          name: 'Alior Bank',
          description: `[Alior Bank](https://www.aliorbank.pl/dodatkowe-informacje/kariera/praca-w-it.html) jest jednym z najdynamiczniej rozwijających się banków uniwersalnych, świadczącym pełen zakres usług skierowanych do klientów detalicznych i biznesowych.  
Bank systematycznie umacnia pozycję rynkową, skutecznie łącząc zasady tradycyjnej bankowości z innowacyjnymi rozwiązaniami i produktami. O najwyższą jakość produktów i usług Banku dba ponad 8000 pracowników. W 2017r. została ogłoszona strategia banku na lata 2017-2020 pod hasłem "Cyfrowy buntownik", co oznacza technologiczną transformację każdego obszaru działania banku. W ramach przyjętej strategii są już realizowane projekty w obszarze:

automatyzacji  
robotyzacji  
blockchain  
chmura  
PSD2/API  
sztuczna inteligencja DRONN  
biometria  
Alior Bank jest jedną z najbardziej rozpoznawalnych marek na polskim rynku bankowym. Ikoną Banku, która łączy świat tradycyjnej bankowości i nowatorskich rozwiązań jest postać Bankiera w meloniku. Dodatkowo podkreśla to hasło „WYŻSZA KULTURA. BANK NOWOŚCI.”.

Dzięki bliskiej współpracy pomiędzy działami, szybkości działania, skracaniu dystansu między szeregowymi pracownikami a zarządem, mamy poczucie, że pracujemy w wyjątkowej firmie. To miejsce dla ludzi, którzy mają pomysły i odwagę biznesową, by wyznaczać nowe standardy bankowości.

Od 2014 r. akcje Alior Banku wchodzą w skład indeksu WIG20 skupiającego największe i najbardziej płynne spółki notowane na Giełdzie Papierów Wartościowych w Warszawie.

Alior Bank jest wielokrotnym laureatem prestiżowych nagród oraz zwycięzcą rankingów o zasięgu międzynarodowym. Ważniejsze osiągnięcia z 2018 roku:

 1\\. miejsce w rankingu „Przyjazny Bank Newsweeka 2018” w kategorii „Bankowość internetowa”,  
1\\. miejsce w kategorii „New Digital Venture” w konkursie The Heart Corporate Innovation Awards 2018 za partnerstwo Alior Banku i cyfrowej platformy pośrednictwa finansowego online Bancovo,
tytuł Lidera Informatyki 2018 w 22. edycji konkursu organizowanego przez magazyn „Computerworld”. Głównymi kryteriami wyboru jest biznesowa wartość projektów dostarczanych przez IT oraz ich innowacyjność,
nagroda honorowa w kategorii „Banki innowacyjne” w rankingu 50. największych banków w Polsce 2018 roku.`,
          www: 'https://www.aliorbank.pl/dodatkowe-informacje/kariera/praca-w-it.html',
          type: 'silver',
          orientation: 'horizontal',
          logo: require('../assets/partners/alior.svg'),

        },{
          id: 'coi',
          name: 'Centralny Ośrodek Informatyki',
          description: `Są tacy, dla których przy tworzeniu usług cyfrowych najważniejszy jest człowiek. Jest miejsce, gdzie dzięki technologii rozproszone instytucje łączą się w proste usługi przyjazne dla obywateli – tak wygląda kod do cyfryzacji. W Centralnym Ośrodku Informatyki realizujemy projekty dla Ministerstwa Cyfryzacji.  
Jesteśmy unikalnym w skali państwa zespołem liczącym ponad 500 specjalistów.  
Zatrudniamy ekspertów IT  
Zarządzamy Systemem Rejestrów Państwowych  
Projektujemy e-usługi  
Prowadzimy projekty  
Dzielimy się wiedzą  
 
Mamy swoje biura w  Warszawie, Lublinie, Katowicach, Łodzi, Rzeszowie, Poznaniu, Bydgoszczy i Gorzowie Wielkopolskim.  
\\#cyfryzacjatofrajda  
My już jesteśmy częścią cyfryzacji, a Ty? Przetestuj [coi.gov.pl](https://www.coi.gov.pl/).`,
          www: 'https://www.coi.gov.pl/',
          type: 'silver',
          orientation: 'horizontal',
          logo: require('../assets/partners/coi.svg'),

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
