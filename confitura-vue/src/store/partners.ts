import { Module } from "vuex";
import { LOAD_PARTNER_BY_ID, LOAD_PARTNERS, Partner, RootState } from "@/types";

function filterBy(partners: Partner[], type: string) {
  return shuffle(partners.filter(partner => partner.type === type));
}

export const partnersModule: Module<PartnersState, RootState> = {
  state: {
    partners: []
  },
  getters: {
    platinum: ({ partners }): Partner[] => filterBy(partners, "platinum"),
    path: ({ partners }): Partner[] => filterBy(partners, "path"),
    silver: ({ partners }): Partner[] => filterBy(partners, "silver"),
    gold: ({ partners }): Partner[] => filterBy(partners, "gold"),
    bronze: ({ partners }): Partner[] => filterBy(partners, "bronze"),
    media: ({ partners }): Partner[] => filterBy(partners, "media"),
    tech: ({ partners }): Partner[] => filterBy(partners, "tech")
  },
  actions: {
    [LOAD_PARTNERS]({ state }) {
      // tslint:disable
      state.partners = [
        {
          id: "virtuslab",
          description: `We are VirtusLab – 300+ tech enthusiasts constantly striving for growth. We provide expert software engineering and consultancy services to help customers adopt technology transformations. Our aim is to solve complex problems and improve the dev's efficiency. We are building high-quality software in various domains: ranging from smart contracts to investment platforms to compiler servers to brick & mortar retail stores. Our team has extensive knowledge about Data Engineering & Data Science, Cloud-Native Services, Reactive Systems, Dev Tooling, and Frontend.

We are also worldwide experts in Scala language, officially supporting its development and tooling starting with Scala 3 itself to tailor-made solutions for the largest Scala monorepos in the world, like Metals (Language Server Protocol) or scalafmt (Code formatter).

Our  #VLteam delivers high-quality software solving actual problems with modern technology and gives back to the community by supporting open source initiatives, local tech meetups, and conferences (check our own initiative, [Sphere.it](https://sphere.it)). 

We have built a company where everyone can influence how we operate and what we are aiming at. We believe that everyone should participate and create new directions.  Visit [virtuslab.com](https://virtuslab.com) for more!`,
          name: "VirtusLab",
          www: "https://virtuslab.com/",
          type: "platinum",
          logo: require("../assets/partners/2022/virtuslab.svg"),
          orientation: "horizontal"
        },
        {
          id: "allegro",
          name: "Allegro Tech",
          description: `W [Allegro](https://allegro.tech/) tworzymy i utrzymujemy aplikacje, które dzięki swojej skalowalności i niezawodności zyskały sobie rzeszę wiernych użytkowników. Zadanie nie należy do najłatwiejszych, w pracy napotykamy na szereg wyzwań zarówno w obszarze samej architektury i designu, jak i w procesie doboru technologii, zapewnienia jakości kodu, usprawnienia funkcjonalności oraz w późniejszej fazie wdrożenia i utrzymania produktu.
          allegro.tech jest naszym pomysłem na dzielenie się zebranym doświadczeniem poprzez organizację konferencji, warsztatów, meetupów i hackatonów.
        
Po więcej informacji zapraszamy na nasz blog [https://allegro.tech/](https://allegro.tech/)
        
Dołacz do nas również na [https://www.meetup.com/allegrotech/](https://www.meetup.com/allegrotech/)
        
I bądź na bieżąco [https://www.facebook.com/allegro.tech](https://www.facebook.com/allegro.tech)`,
          www: "https://allegro.tech/",
          type: "silver",
          logo: require("../assets/partners/allegro.svg"),
          orientation: "horizontal"
        },
        {
          id: "scalo",
          description: `[Scalo](https://www.scalosoft.com/) to polska firma, w której realizujemy kompleksowe projekty programistyczne, budujemy dedykowane zespoły deweloperskie, rozwijamy systemy wbudowane dla klientów z różnych branż. W naszej firmie każdy może **działać swobodnie**, jesteśmy otwarci na nowe pomysły i rozwiązania. Stawiamy na **współpracę**, chętnie **dzielimy się wiedzą** i wymieniamy **doświadczenia projektowe**.

Nasz sukces to **zgrany zespół**. Wśród nas znajdziesz developerów i projektantów, analityków, testerów oraz administratorów. Biznes wspieramy siłami kilkudziesięciu handlowców, rekruterów i specjalistów od delivery, którzy są dostępni w każdym momencie.

Jeżeli nie przekonują Cię opisy to niech przemówią liczby. W tegorocznym badaniu Great Place to Work uzyskaliśmy **78%** poziom zaangażowania, a do naszych najmocniejszych stron należą:
- **98%** z nas twierdzi, że każdy nowy pracownik spotyka się w Scalo z ciepłym przyjęciem,
- **94%** z nas twierdzi, że menedżerowie dostrzegają w członkach zespołu człowieka, a nie tylko pracownika,
- **93%** z nas twierdzi, że w Scalo panuje przyjazna atmosfera,
- **92%** z nas twierdzi, że Scalo stwarza korzystne warunki dla pogodzenia pracy z obowiązkami rodzica lub opiekuna,
- **88%** z nas twierdzi, że w Scalo można liczyć na wzajemną współpracę.

Dołącz do nas i stwórz swoją historię w Scalo! Odwiedź naszą stronę internetową i poznaj nas bliżej: www.career.scalosoft.com`,
          name: "Scalo",
          www: "https://www.career.scalosoft.com/",
          type: "silver",
          logo: require("../assets/partners/2022/scalo.svg"),
          orientation: "horizontal"
        },
        {
          id: "nordea",
          name: "Nordea",
          description: `W Nordea na co dzień pracują specjaliści, którzy dbają o bezpieczeństwo i sprawne funkcjonowanie banku Nordea. Nordea zatrudnia w Polsce ekspertów w obszarach m.in. IT, bankowości, audytu, rynków kapitałowych, robotyki, AML i sankcji.

https://www.nordea.com/en
`,
          www: "https://www.nordea.com/en",
          type: "silver",
          logo: require("../assets/partners/2022/nordea.svg"),
          orientation: "horizontal"
        },
        {
          id: "dataart",
          name: "DataArt",
          description: `DataArt zajmuje się inżynierią oprogramowania na skalę globalną. Od ponad 20 lat zespoły składające się z wysoko wykwalifikowanych specjalistów tworzą rozwiązania technologiczne, które pomagają klientom osiągać cele biznesowe i zdobywać nowe rynki. Najważniejszą wartością w DataArt są ludzie. Tę zasadę stosujemy odpowiadając na potrzeby klientów, niezależnie od skali ich biznesu i stosowanych technologii. Dostosowujemy się do kierunku rozwoju naszych partnerów biznesowych i ewoluujemy wraz z nimi. W DataArt łączymy doskonałość techniczną z wartościami, które kształtują nasz model biznesowy: ciekawością, otwartością, zaufaniem, szczerością i intuicją. Takie podejście pozwala nam dostarczać wartościowe rozwiązania wysokiej jakości i budować partnerskie relacje, na których nasi klienci mogą polegać.

DataArt zdobyła zaufanie wiodących marek i wymagających klientów takich jak Nasdaq, S&P, oneworld Alliance, Ocado, artnet, Betfair i skyscanner. Funkcjonuje jako globalna sieć firm świadczących usługi technologiczne, współpracując z ponad 5000 profesjonalistami w ponad 20 lokalizacjach w USA, Europie i Ameryce Południowej.

W Polsce DataArt posiada biura w czterech miastach - w Lublinie, Wrocławiu, Krakowie i Łodzi.
`,
          www: "https://www.dataart.com.pl/",
          type: "silver",
          logo: require("../assets/partners/2022/dataart.svg"),
          orientation: "horizontal"
        },
        {
          id: "coderslab",
          name: "Coders Lab",
          description: `Coders Lab to najstarsza szkoła IT w Polsce. W jej ofercie znajdują się kursy z zakresu programowania front-end i back-end, testowania manualnego i automatyzującego oraz analityki danych. Łącząc doświadczenie edukacyjne ze znajomością rynku pracy IT, Coders Lab umożliwia realną zmianę zawodową osobom początkującym, jak i rozwój programistom posiadającym doświadczenie. W ciągu 8 lat jej kursy ukończyło ponad 8 000 absolwentów.`,
          www: "https://coderslab.pl/",
          type: "media",
          logo: require("../assets/partners/2022/coderslab.svg"),
          orientation: "horizontal"
        },
        {
          id: "bnpparibas",
          name: "BNP Paribas",
          description: `BNP Paribas działa w pierwszej 6 największych banków w Polsce pod względem kapitałowym dostarczając 4 mln naszych Klientów rozwiązania z zakresu finansowania działalności na rynku krajowym i międzynarodowym oraz szereg produktów oszczędnościowo-inwestycyjnych i kredytowych.

Na Confitura pojawiamy się nie tylko jako bank, ale przede wszystkim jako pracodawca z obszaru IT, bo obszar Technologii jest kluczowy w funkcjonowaniu naszego biznesu. Bankowość to nie tylko działy sprzedaży czy operacji. Nasz bank to również miejsce, w którym pracownicy mogą być odpowiedzialni za rozwój nowoczesnych aplikacji internetowych i mobilnych, budowanie innowacyjnych rozwiązań z wykorzystaniem Big Data, Cloud oraz AI, modernizację i utrzymanie infrastruktury, wsparcie użytkowników i wiele innych.

Praca w BNP Paribas to praca w środowisku bardzo dynamicznym i pełnym wyzwań, dającym możliwość współpracy z największymi firmami z branży technologii oraz z szeregiem innowacyjnych startupów zarówno z Polski jak i ze świata. Chcesz sprawdzić jakie możliwości rozwoju dajemy kandydatom? Wejdź na stronę [Praca w IT | BNP Kariera.](https://www.bnpparibas.pl/kariera/pl/obszary-zatrudnienia/praca-w-it)`,
          www: "https://www.bnpparibas.pl",
          type: "gold",
          logo: require("../assets/partners/2022/bnpparibas.svg"),
          orientation: "horizontal"
        },
        {
          id: "decerto",
          name: "Decerto",
          description: `Jesteśmy polskim software house’m obecnym na rynku od 2006 roku. Realizujemy projekty dla dużych korporacji, głównie z sektora finansowego. Równocześnie zachowujemy płaską strukturę i niekorporacyjną kulturę pracy. Posiadamy duże doświadczenie i know-how oraz stawiamy na długoterminowe relacje. Dzięki zaufaniu Klientów prowadzimy kilkanaście dużych projektów – pracując z nami zyskujesz nie tylko stabilność zatrudnienia, ale również duże możliwości rozwoju oraz wybór odpowiedniego środowiska pracy. Specjalizujemy się w rozwiązaniach backendowych związanych z dużymi wolumenami danych oraz logiką biznesową. Projektujemy kompleksowe rozwiązania informatyczne, dlatego cenimy otwarty umysł, samodzielność oraz własną inicjatywę. Doceniamy indywidualizm i szanujemy inny punkt widzenia.`,
          www: " https://www.decerto.com/pl/start",
          type: "silver",
          logo: require("../assets/partners/2022/decerto.svg"),
          orientation: "horizontal"
        },
        {
          id: "bigpicture",
          name: "BigPicture",
          description: `Hi, we’re BigPicture, the proud creators of one of the leading initiative management software on the market. Our vision is shared by over 3 million users of BigPicture in organizations of all sizes: from small startups to market leaders such as Netflix, NASA, Intel, Tesla, Apple, Uber, Samsung, Porsche, Siemens, LG, and many more.

So far, we’ve built an amazing team of 200 experts. Join us!

How we work
- In Agile and Scrum we trust - we make sure to keep our teams small and our sprints manageable & effective
- Ownership - we want everyone on our team to feel empowered to grab the reins in their positions and help push the company forward
- We can set our own working hours. We just need to make sure to join our team for the daily meeting
- We work 100% remotely or from our modern office in Warsaw - we can choose

Our solutions stack
- We use monorepo to maintain consistency of development through automated validations in one of the most extensive CI processes in the local market providing not only static/dynamic code analysis, but Architecture Decision Records appliance as well
- We support ~10000 users for a single tenant deployed on our own multitenant cloud infrastructure
- Java with a fully fledged and standardized DDD including Hexagonal Architecture, Event Sourcing, hundreds of Aggregates spanning dozens of domains, Ubiquitous language implemented across the whole organization
- And for all of you frontend lovers out there, it's worth knowing that this is one of the largest and most complex Angular projects in Poland

We focus on the continuous development of our skills. That is why we invest in and offer:
- Unlimited skills development budget
- ShipIT Weeks - regular, internal, one week long, fully-paid hackathons
- Internal tech guilds - meetings dedicated to sharing knowledge, and best practices
- Taking part in tech events sponsored by us - like Confitura ;)

Check our open positions here: https://bigpicture.one/careers/

Follow BigPicture's #BigTeam here:
- [Instagram](https://www.instagram.com/bigpicture.one/)
- [Facebook](https://www.facebook.com/BigPicture.one)
- [LinkedIn](https://www.linkedin.com/company/bigpictureone/)
`,
          www: " https://bigpicture.one/",
          type: "platinum",
          logo: require("../assets/partners/2022/bigpicture.svg"),
          orientation: "horizontal"
        },
        {
          id: "softwaremill",
          name: "SoftwareMill",
          description: `Hello from [SoftwareMill](https://softwaremill.com/) - a consulting & custom software agency that proactively helps businesses benefit from technologies like Cloud Computing, Big Data, Blockchain and Machine Learning.

We specialise in Java, Scala, Akka, the Lightbend Reactive Platform, Kafka, Spark, TensorFlow, SQL & NoSQL databases: PostgreSQL, Cassandra, Mongo, Node.JS and React. We build on the traditional engineering work ethics and values, which translate directly to the quality of our systems. We are a leading consultancy chosen for digital transformation, recognized for our integrity, versatility, understanding of the business, right soft skills, strong work ethic, rich experience and top notch mastery of technology.

Widening technical horizons is the fabric of our organisation. Not only do we practise and learn constantly, but we also teach, create and contribute. We have a proven track record of working with the IT community, leading open-source projects [[why not to contribute?]](https://github.com/softwaremill/) and organising a [Scalar conference](https://scalar-conf.com/).

Our priority is to deliver quality software: thoroughly tested, well-designed, and reviewed. But, our growth is not limited to technology. Since 2009 we have been building the best, fully remote, workplace for developers. We have a unique, 100% remote and bottom-up structure where leaders are not appointed, but chosen by teammates. In 2022, we’ve grown into a company of 80+ people. We simply like each other and together we create [a friendly environment](https://softwaremill.com/about-us/) that enhances our potential.

We’re eager to welcome more passionate software engineers to our Team. If you want to share your knowledge, empower teammates and lead by example, while working on interesting projects, check out our [career page](https://softwaremill.com/join-us/)!

Catch #SoftwareMillVibes
- [SoftwareMill Tech Blog](https://softwaremill.com/blog/)
- [Scala Times newsletter](https://scalatimes.com/)
- [Scala 3 Tech Report](https://softwaremill.com/ebooks/scala-3-tech-report/)
- [SoftwareMill Academy](https://academy.softwaremill.com/en/)
- [AMA Slack](https://sml.io/ask-me-anything)
- [Instagram](https://instagram.com/softwaremill_vibes)
- [Youtube](https://www.youtube.com/c/SoftwareMillCom)
- [Facebook](https://www.facebook.com/softwaremill)
- [Twitter](https://twitter.com/softwaremill)
- [Linkedin](https://www.linkedin.com/company/808422)
- [DevSkin Softwear](https://devskin-softwaremill.myspreadshop.pl/)
`,
          www: "https://softwaremill.com/",
          type: "silver",
          logo: require("../assets/partners/2022/sml2.svg"),
          orientation: "horizontal"
        },
        {
          id: "intercars",
          name: "Inter Cars",
          description: `[Inter Cars](https://intercars.com.pl/) has been a leading distributor of spare parts for passenger cars and trucks on the Polish market for many years. In addition, the company has gradually been developing its activity in 18 countries across Europe, ranking first in the Central and Eastern part of the continent, second on the entire continent and eighth globally.

Inter Cars offers over two million spare parts for passenger cars and trucks, parts for motorcycles, farming and industrial machines as well as driver accessories, with as many as 40,000 new items every years being introduced into the sales system for the trucks market alone.

The company provides garages fast, convenient and safe access to all tools facilitating their operations as, in addition to spare parts for cars, Inter Cars offers technical support, innovative garage management programs as well as stationary and online training.

In the entire IT area at Inter Cars, we see the need to constantly improve the competences of IT Managers and Employees. Every year, we have a specific development budget allocated to this need. We are committed to developing both managerial and technical competences as well as the so-called "Soft" related to, inter area, with communication, cooperation, improvement of individual and team effectiveness.

Inter Cars IT department currently consists of about 300 employees and we are still looking for new ones! Teams that you can confidently join, if you are a person open to new challenges, you will surely find an area for yourself.`,
          www: "https://intercars.com.pl/",
          type: "silver",
          logo: require("../assets/partners/2022/intercars.svg"),
          orientation: "horizontal"
        },
        {
          id: "bosch",
          description: `The Bosch Group is a leading global supplier of technology and services. It employs roughly 402,600 associates worldwide (as of December 31, 2021). The company generated sales of 78.7 billion euros in 2021. Its operations are divided into four business sectors: Mobility Solutions, Industrial Technology, Consumer Goods, and Energy and Building Technology. As a leading IoT provider, Bosch offers innovative solutions for smart homes, Industry 4.0, and connected mobility. Bosch is pursuing a vision of mobility that is sustainable, safe, and exciting. It uses its expertise in sensor technology, software, and services, as well as its own IoT cloud, to offer its customers connected, cross-domain solutions from a single source. The Bosch Group’s strategic objective is to facilitate connected living with products and solutions that either contain artificial intelligence (AI) or have been developed or manufactured with its help. Bosch improves quality of life worldwide with products and services that are innovative and spark enthusiasm. In short, Bosch creates technology that is “Invented for life.”`,
          name: "Bosch",
          www: "https://www.bosch.com/company/",
          type: "silver",
          logo: require("../assets/partners/2022/bosch.svg"),
          orientation: "horizontal"
        },
        {
          id: "kmd",
          name: "KMD",
          description: `KMD Poland is a foreign branch of the KMD Group which is one of the largest Danish IT companies. The company specializes in implementing key IT solutions and processes in energy area, finance, insurance, R&D and HR. Its key projects are implemented in the public sector in Denmark and in Scandinavian countries, contributing to the digital transformation of their societies, public institutions and private sector entities. The KMD Group has more than 3,000 employees and it is a subsidiary of NEC Corporation, a global leader in integration of IT and networking technologies. This year, KMD Group is celebrating its 50<sup>th</sup> anniversary.`,
          www: "https://kmdpoland.pl",
          type: "silver",
          logo: require("../assets/partners/2022/kmd.svg"),
          orientation: "horizontal"
        },
        {
          id: "goldman-sachs",
          name: "Goldman Sachs",
          description: `At Goldman Sachs, our Engineers don’t just make things – we make things possible. Join our engineering teams that build massively scalable software and systems, architect low latency infrastructure solutions, proactively guard against cyber threats, and leverage machine learning alongside financial engineering to continuously turn data into action.`,
          www: "https://www.goldmansachs.com/careers/",
          type: "gold",
          orientation: "horizontal",
          logo: require("../assets/partners/2022/goldmansachs.svg")
        },
        {
          id: "onwelo",
          name: "Onwelo",
          description: `Onwelo to nowoczesna polska spółka technologiczna, która specjalizuje się w budowaniu innowacyjnych rozwiązań IT dla organizacji z szeregu sektorów na całym świecie. Główne obszary działalności Onwelo to: tworzenie oprogramowania, jego rozwój oraz utrzymanie, a także mocne wsparcie kompetencyjne. W krótkim czasie firma wdrożyła ponad 300 projektów w Europie i w USA, powiększyła zespół do ponad 400 osób, a także otworzyła biura w sześciu miastach Polski oraz oddziały w Stanach Zjednoczonych, Niemczech i w Szwajcarii. 
 
Chociaż filar naszej firmy stanowią doświadczeni, wykwalifikowani specjaliści, jesteśmy również otwarci na włączenie do naszych zespołów pasjonatów IT, którzy przy naszym wsparciu chcą zmienić dotychczasowy stack lub postawić swoje pierwsze kroki w świecie zaawansowanych technologii. W Onwelo zatrudniamy specjalistów z obszarów: analizy biznesowej, rozwoju i utrzymania oprogramowania, testowania oprogramowania, zarządzania infrastrukturą IT, tworzenia rozwiązań Data & Analytics oraz Intelligent Automation, a także rozwiązań chmurowych. 

Dlaczego warto u nas pracować?

* **Wsparcie mentorskie i jasne ścieżki kariery** – możesz liczyć na wsparcie naszych ekspertów i przełożonych wywodzących się z obszarów merytorycznych oraz na kulturę wymiany wiedzy i spersonalizowany rozwój
* **Nowoczesne technologie i narzędzia pracy** – realizujemy projekty o wysokim stopniu zaawansowania technologicznego, a dzięki nowoczesnym standardom narzędzi pracy i systemów, Twoja praca jest bardziej przyjemna i rozwojowa
* **Projekty zagraniczne** – pracuj z nami dla klientów z Europy Zachodniej i Stanów Zjednoczonych
* **Zgrany zespół i inicjatywy integracyjne** – lubisz planszówki, warsztaty, wyzwania sportowe albo spontaniczne wyjścia w większym gronie? Zadbamy o to!
* **Elastyczny model zatrudnienia i możliwość pracy zdalnej** – dostosujemy model zatrudnienia do Twoich potrzeb. Jako samodzielny specjalista możesz pracować zarówno w jednym z naszych nowoczesnych biur w atrakcyjnej lokalizacji, jak i zdalnie
* **Przyjazny onboarding** – zadbamy o to, żeby Twój start w firmie był łatwy i przyjemny
* **Pakiety benefitów** – karta MultiSport, prywatna opieka medyczna, grupowe ubezpieczenie na życie – to u nas standard

Chcesz dowiedzieć się o nas więcej? Wejdź na https://onwelo.com/career,p6.html`,
          www: "https://www.onwelo.com",
          type: "gold",
          orientation: "horizontal",
          logo: require("../assets/partners/2022/onwelo.svg")
        },
        {
          id: "touk",
          name: "Touk",
          description: `TouK to miejsce twórczej eksploracji dla deweloperów. Tu liczy się pasja, inicjatywa i (współ)działanie. Tworzymy przestrzeń, gdzie ludzie o różnych charakterach, niezależnie od doświadczenia, mają możliwość pracować i rozwijać się przez lata. Nasza kultura, doświadczony i wszechstronny zespół owocują realizacją coraz ciekawszych i ambitniejszych projektów.`,
          www: "https://touk.pl/careers",
          type: "silver",
          orientation: "horizontal",
          logo: require("../assets/partners/touk.svg")
        },
        {
          id: "citi",
          name: "Citi",
          description: `Citi is the leading global bank with a history spanning more than 200 years and which serves more than 200 million customer accounts and does business in more than 160 countries and jurisdictions. Citi provides consumers, corporations, governments and institutions with a broad range of world-class financial products and services, including consumer banking and credit, corporate and investment banking, securities brokerage, transaction services and wealth management.

Our technology teams deliver customized solutions by utilizing cutting edge technologies to deploy everything from cloud computing to mobile solutions to APIs, creating a competitive advantage for Citi, our clients, our regulators and our stakeholders. In this fast-paced environment, our employees are focused on creating solutions, including CitiDirect BE® Mobile for transaction services and Citi Velocity, an electronic trading platform, that provide Citi's institutional clients with unprecedented access to capital markets intelligence and execution.

In Poland we act under Citibank Europe. We opened in 2005 with just 57 employees; today we are a diverse team of almost 6000 serving 96 countries from all continents. We work across the full spectrum of Citi business areas, including Technology.

Learn more at: [jobs.citi.com/Poland](http://jobs.citi.com/Poland)
`,
          www: "http://jobs.citi.com/Poland",
          type: "gold",
          orientation: "box",
          logo: require("../assets/partners/2022/citi.svg")
        },
        {
          id: "sii",
          name: "Sii Poland",
          description: `Since 2006 on the market, 7 000 experts, PLN 1 billion revenue, 8 times Great Place to Work title – get to know Sii, the fastest growing IT, digital transformation, BPO and engineering company in Poland.

Sii already has over 1,000 Java developers who carry out projects for brands such as PUMA, Ingenico, Scalepoint, Sennheiser, Berlingske Media, ABB Sartorius, Qiagen every day. Specialists from Sii’s Digital Competency Center join projects in which the majority of the work covers the development of new products and functionalities. Apart from internal communities, the team has various knowledge sharing and professional growth opportunities, including organized training sessions and numerous partnerships, among others with Adobe. Learn more at [sii.pl](https://sii.pl).`,
          www: "https://sii.pl",
          type: "silver",
          orientation: "horizontal",
          logo: require("../assets/partners/2022/sii.svg")
        },
        {
          id: "rsystems",
          name: "R Systems",
          description: `[R Systems](https://eu.rsystems.com/) is a global technology and analytics services company. We help our clients achieve speed-to-market, overcome digital barriers, and create business value with our specialized service offerings and consultative business approach.

We speak the language of business as fluently as we do the language of technology. In other words: **We speak Digital**. Our goal: accelerate our clients’ digital leadership.`,
          www: "https://eu.rsystems.com/",
          type: "silver",
          orientation: "horizontal",
          logo: require("../assets/partners/2022/rsystems.svg")
        },
        {
          id: "sages",
          name: "Sages",
          description: `[Sages](https://www.sages.pl) od 2007 r., zapewnia kompleksowe wsparcie w zakresie IT. Zajmujemy się kształceniem specjalistów IT oraz tworzeniem rozwiązań opartych na sztucznej inteligencji mających na celu automatyzację procesów w organizacjach.
 
Wspólnie z Politechniką Warszawską stworzyliśmy system Omega-PSIR do ewaluacji uczelni i instytucji naukowych. Jest to nr 1 w Polsce i nr 5 na świecie, jeżeli chodzi o liczbę wdrożeń pośród oprogramowania tego typu.
W ramach oferty edukacyjnej organizujemy szkolenia dla programistów, testerów, integratorów, managerów, analityków ze wszystkich obszarów związanych z realizacją projektów IT.
W obszarze sztucznej inteligencji realizujemy projekty związane z przetwarzaniem języka naturalnego, analizą obrazu i automatyzacją procesów, takie jak: chatboty do obsługi klienta zewnętrznego, chatboty do wewnętrznej obsługi procesów w firmie, wyszukiwarki semantyczne, czy ekstrakcja informacji z nieustrukturyzowanych źródeł takich jak dokumenty i skany. Tworzymy narzędzie dla osób niewidzących i niedowidzących do automatycznego odczytu dokumentów tekstowo-obrazowych.
 
Angażujemy się intensywnie w działania na rzecz społeczności IT oraz nauki: od wielu lat moderujemy społeczność Stacji IT, współorganizujemy konkurs PolEval, konferencję AI & NLP Day oraz Seminarium Użytkowników Uczelnianych Baz Wiedzy
`,
          www: "https://www.sages.pl",
          type: "silver",
          orientation: "horizontal",
          logo: require("../assets/partners/2022/sages.svg")
        },
        {
          id: "syncron",
          name: "Syncron",
          description: `Syncron is a global IT product company with Swedish roots, but it is in the Warsaw office where our flagship product - Inventory - has been developed.

We create proprietary Java-based applications in the SaaS model.

They are used to help the world’s leading manufacturers to optimize all the processes around the heavy machinery service lifecycle: spare parts inventory management and supply chain, parts pricing, machine uptime, field service and warranty management.

The highest coding standards are our priority. We are a product company and the quality of our software directly impacts our customers’ revenues, so we thrive to produce a high-quality code with all kinds of tests including unit, integration and end-to-end tests, in the face of constant optimization challenges. We are fully cloud-based, using advanced AWS infrastructure solutions.

Check out our open vacancies: [https://www.syncron.com/company/careers/open-positions/](https://www.syncron.com/company/careers/open-positions/)`,
          www: "https://www.syncron.com/",
          type: "silver",
          orientation: "horizontal",
          logo: require("../assets/partners/2022/syncron.svg")
        },
        {
          id: "match-trade",
          name: "Match-trade",
          description: `Jesteśmy firmą z branży fintech, tworzącą zaawansowane systemy transakcyjne dla rynku Forex, kontraktów CFD, kryptowalut oraz akcji. Tworzymy własne środowisko technologiczne i jesteśmy autorem platformy tradingowej opartej o technologię PWA. Współpracujemy z klientami z całego świata - w tym największymi brokerami Forex i najbardziej znanymi giełdami kryptowalut. W ramach jednego z naszych głównych projektów, zajmujemy się budową algorytmów z zakresu High Frequency Trading. Algorytmy te każdego dnia obracają kryptowalutami wartymi miliony dolarów. Nasze rozwiązania są wykorzystywane na giełdach z aż pięciu kontynentów. Członkowie naszego zespołu, nierzadko pasjonaci technologii blockchain oraz kryptowalut, każdego dnia zapoznają się z nowinkami tej nowej, dynamicznie rozwijającej się części sektora finansowego. Śmiało możemy powiedzieć też, szeroki zakres oferowanych przez nas usług stwarza dla naszych pracowników możliwości uczestniczenia w różnorodnych projektach, nie tylko dzięki wykorzystaniu różnych technologii (w tym nowoczesnej technologii Blockchain), ale także dzięki pracy z biznesami z całego świata.`,
          www: "https://match-trade.com/",
          type: "silver",
          orientation: "horizontal",
          logo: require("../assets/partners/2022/match-trade.svg")
        },
        {
          id: "fis",
          name: "FIS",
          description: `FIS is the world’s largest provider of banking and payments technology solutions and a global leader in consulting and outsourcing solutions. With a long history deeply rooted in the financial services sector, FIS serves more than 14,000 institutions in over 130 countries. Headquartered in Jacksonville, Fla., FIS employs more than 55,000 people worldwide and holds leadership positions in payment processing and banking solutions, providing software, services and outsourcing of the technology that drives financial institutions. In October 2014 FIS completed acquisition of Brussels-based Clear2Pay. The transaction brings new corporate payment solutions and services, inclusive of high-value and cross-currency corporate payments, payments managed services, and payments processing utilities that will further bolster FIS’ payments portfolio across all geographies. Clear2Pay is now an FIS company.`,
          www: "https://www.fisglobal.com/en/",
          type: "silver",
          orientation: "horizontal",
          logo: require("../assets/partners/2022/fis.svg")
        },
        {
          id: "cgi",
          name: "CGI",
          description: `**Mamy ofertę na miarę Twoich kwalifikacji. Zbudujmy wspólnie zespół idealnie dopasowany do Ciebie.**

Chcesz pracować z najlepszymi ekspertami w branży, obcować z najnowocześniejszą technologią i wdrażać imponujące projekty, które zmieniają rzeczywistość? Róbmy to razem!

[Dołącz do CGI](https://www.cgi.com/polska/pl/kariera). Stań się częścią jednej z największych na świecie firm konsultingowych w obszarze IT, a jeśli zechcesz, również jej współwłaścicielem. Wraz ze swoim

zespołem możesz tworzyć rozwiązania, które ułatwiają i usprawniają życie. Robimy to od 46 lat na świecie i od 25 lat w Polsce. Oto tylko kilka przykładów naszych działań:

* Wspieraliśmy uruchomienie sieci Plus GSM w Polsce
* Stworzyliśmy sprawny system zarządzania relacjami z klientami dla wiodącego dewelopera w Polsce
* Usprawniamy działalność instytucji finansowych poprzez wdrażanie najnowocześniejszych rozwiązań, między innymi regulacji IFRS9
* Jesteśmy liderem w rozwijaniu i implementacji core’owych systemów ubezpieczeniowych, takich jak IBA i TIA
* W Polsce Tworzymy środowisko chmurowe, które będzie obsługiwało 50 skandynawskich instytucji finansowych

**CGI to historia setek imponujących karier**

Interesuje Cię rozwój kariery na własnych zasadach? Jeśli tak, to CGI jest miejscem dla Ciebie. Zróżnicowane projekty i możliwości w ramach CGI dają Ci szansę na samorealizację w kierunkach, które interesują Cię najbardziej. Podążaj własną ścieżką mając wsparcie prawie 500 najlepszych specjalistów. Nie ma takiej technologii ani branży, na której się nie znamy Reprezentujemy poziom ekspercki. Będziesz więc otoczony koleżankami i kolegami o najwyższych kwalifikacjach, jak w mistrzowskiej drużynie.
`,
          www: "https://www.cgi.com/polska/pl/kariera",
          type: "gold",
          orientation: "horizontal",
          logo: require("../assets/partners/2022/cgi.svg")
        },
        {
          id: "revolut",
          name: "Revolut",
          description: `# About [Revolut](https://www.revolut.com/)

People deserve more from their money. More visibility, more control, more freedom. And since 2015, Revolut has been on a mission to deliver just that. With an arsenal of awesome products that span spending, saving, travel, transfers, investing, exchanging and more, our super app has helped over 18 million customers get more from their money. And we're not done yet!


## About our Technology Department

Our Technology team isn’t just one of the best in the industry, it’s one of the best in the world. And we’re proud of it. It’s our driving force; our engine 🚀 

From building a new banking backend to creating an award-winning app, there’s nothing (and we mean nothing) our tech team can’t do. Our Technology team isn’t here to fix legacy systems – it’s here to build world-class financial products from the ground up. Products that will be used by millions of people around the world (18+ million people, not that we’re counting)🌎

We have Engineers that want to change the world. If you like to work at a steady pace with no surprises, keep scrolling. If you want your work to change the global financial landscape, you might be just who we’re looking for. We have a minimalist approach to using external frameworks, with emphasis on maintainability and fast turnaround with TDD, DDD and Continuous Integration & Delivery.  \\
Sound like your speed? Check our openings: [https://www.revolut.com/careers?team=Engineering+%26+Data](https://www.revolut.com/careers?team=Engineering+%26+Data) 

Follow us:



* [https://www.linkedin.com/company/revolut](https://www.linkedin.com/company/revolut)
* [https://medium.com/revolut](https://medium.com/revolut)
* [https://www.facebook.com/RevolutInsider](https://www.facebook.com/RevolutInsider)
* [https://www.instagram.com/revolutinsider/?hl=en](https://www.instagram.com/revolutinsider/?hl=en)`,
          www: "https://www.revolut.com/",
          type: "gold",
          orientation: "horizontal",
          logo: require("../assets/partners/2022/revolut.svg")
        },
        {
          id: "starburst",
          name: "Starburst",
          description: `Starburst is a successful, multinational start-up founded by an experienced group of passionates from the US and Poland. After 5 years of presence on the market we currently have more than 400 employees, including people from UK, Poland, US, Germany, Canada and several more engineers around the world.

For now in our portfolio we have around 200 satisfied clients, including Comcast, Zalando, Finra, Verizon, Societe Generale, Randstad, Sky and many more. We tripled the value of the company 3 years in a row and after last funding round (series D) in February 2022 our valuation reached over $3B!

We unlock the value of distributed data by making it fast and easy to access via SQL, no matter where it lives. Starburst queries data across any database, making it instantly actionable for data-driven organizations. With Starburst, teams can lower the total cost of their  infrastructure and analytics investments, prevent vendor lock-in, and use the existing tools that work for their business. Starburst helps companies make better decisions faster on all data.

Currently for our Poland-based team we are looking for new All-Star engineers who are at the top of their game. If this sounds like you, check out our open roles at [starburst.io/careers](https://www.starburst.io/careers/)

For more information visit our website: [starburstdata.io](https://starburst.io/)

You may also follow our activity on:
- [Linkedin](https://www.linkedin.com/company/starburstdata/)
- [Twitter](https://twitter.com/starburstdata)
- [Facebook](https://www.facebook.com/starburstdata)
`,
          www: "https://starburst.io/",
          type: "silver",
          orientation: "horizontal",
          logo: require("../assets/partners/2022/starburst.svg")
        },
        {
          id: "grid-dynamics",
          name: "Grid Dynamics",
          description: `Grid Dynamics is a leading provider of technology consulting, engineering, and data science services for Fortune 500 corporations from financial, technological and retail sectors. It has been operating since 2006, is headquartered in the San Francisco area (San Ramon), California, and has development centers in the United States and Eastern Europe.  

As of today, Grid Dynamics brings together more than 3,500 engineers in 13 countries (USA, Mexico, Jamaica, Netherlands, England, Switzerland, Romania, Serbia, Armenia, Ukraine, Poland, Moldova, India) and continues to grow steadily. The clients include Google, Apple, Microsoft, Tesla, PayPal, and other technology giants.

Grid Dynamics provides unique opportunities for students, fresh graduates and people who want to change their career path. Duration of the Internship is 6 months and it contains training in technical areas. Our interns can expect 30h per week of hands-on technical experience with a flexible schedule.

Check our open positions here: [http://careers.griddynamics.com/](http://careers.griddynamics.com/?utm_source=confitura)

Follow us here:
 - [Instagram](https://www.instagram.com/griddynamics_pl/)
 - [Facebook](https://www.facebook.com/lifeatgriddynamics)
 - [LinkedIn](https://www.linkedin.com/company/grid-dynamics)
`,
          www: "http://careers.griddynamics.com",
          type: "silver",
          orientation: "horizontal",
          logo: require("../assets/partners/2022/griddynamics.svg")
        },
        {
          id: "datumo",
          name: "Datumo",
          description: `Jesteśmy firmą konsultingową z zakresu technologii Big Data i Cloud. Istniejemy i działamy od 2017 roku z inicjatywy  Piotra Guzika i Daniela Pogrebniaka. Naszym pierwszym produktem był “Storyteller”, który wraz z biegiem czasu został przekształcony w mniejsze moduły, nadal z sukcesem wdrażane u naszych klientów. Wiemy, że potencjał tkwi w danych dlatego też z zaangażowaniem projektujemy dedykowane platformy Big Data dopasowane do potrzeb klienta. Zapewniamy wsparcie całych zespołów jak i pojedynczych ekspertów.`,
          www: "http://datumo.io/",
          type: "silver",
          orientation: "horizontal",
          logo: require("../assets/partners/2022/datumo.svg")
        }
        // {
        //   id: "softwareplant",
        //   description: `Technologiczny startup rozwijający produkty do nowoczesnego zarządzania projektami
        //   z bazą 3mln użytkowników i ponad 8000 klientów. Wśród obsługiwanych firm znajdują się m.in.
        //   Tesla, Intel, Oracle, Bloomberg, Sony, a także wiele instytucji rządowych, uniwersytetów
        //   i organizacji społecznych.\n\nFirma przywiązuje ogromną uwagę do jakości tworzonego
        //   oprogramowania. Kod pisany jest zgodnie z zasadami Domain Driven Design z typową dla
        //   tego podejścia architekturą heksagonalną. Dekompozycja na domeny generyczne, główną
        //   i pomocnicze umożliwia sprawny rozwój nowych funkcjonalności w stale rosnącym zespole
        //   i kodzie źródłowym, który liczy już ponad 0,7mln linii kodu. Kod domenowy jest wolny
        //   od zależności do środowiska, baz danych, czy modeli ORM, które dostarczane są w warstwie
        //   infrastrukturalnej. Frontend oparty jest na Angular5 Typescript z Webpackiem,
        //   gdzie także gości DDD.\n\nKażda zmiana w kodzie podlega złożonemu procesowi,
        //   którego pierwszym krokiem jest automaczne review angażujące kilkadziesiąt kontenerów
        //   dockerowych, analizatory statyczne, testy jednostkowe oraz integracyjne. Kolejnym krokiem
        //   jest peer review dbające o propagacje wiedzy i wspólne zrozumienie domeny. Końcowe kroki
        //   to rozbudowane pipeline-y tworzące continuous delivery, które pozwalają dostarczać nowe
        //    wersje oprogramowania w bardzo krótkich cyklach.\n\nOstatni rok dla SoftwarePlant to
        //     dynamiczny rozwój klientów w modelu SaaS, dla których firma wykorzystuje farmę serwerów
        //      AWS wraz z Elasticsearch, Influx oraz Grafana do centralnego monitoringu całości
        //       rozwiązania.\n\nOdwiedź nas na stoisku podczas Confitury lub w
        //        [naszym biurze](https://www.youtube.com/watch?v=xNJ-el6bqyg).`,
        //   name: "SoftwarePlant",
        //   www: "https://softwareplant.com/",
        //   type: "platinum",
        //   logo: require("../assets/partners/softwareplant.svg"),
        //   orientation: "horizontal"
        // },
        //                 {
        //                     id: "ey",
        //                     description: `EY Global Delivery Services (GDS) is a network of EY competence centers located in five countries: India, China, Poland, Argentina, and the Philippines. [EY GDS Poland](www.workatgds.pl) provides high quality and value-added support to our customers who are EY firms and other companies, working to agreed principles of exceptional client service. The technology departments of EY GDS Poland include teams working for external clients from around the world, as well as specialists who create and develop internal EY apps.`,
        //                     name: "EY",
        //                     www: "https://workatgds.pl/pl/",
        //                     type: "platinum",
        //                     logo: require("../assets/partners/ey.svg"),
        //                     orientation: "horizontal"
        //                 },
        //                 {
        //                     id: "7n",
        //                     description: `[7N](https://www.7n.com/) to duńska firma konsultingowa, zajmująca się
        //           outsourcingiem ekspertów IT.
        //           Globalnie, w ośmiu krajach zatrudniamy ponad 1000 osób (w Polsce ponad 600).
        //           W ciągu prawie 30 lat działalności dowiedliśmy, że jawny i przejrzysty model finansowy,
        //           współpraca wyłącznie z ekspertami w swoich dziedzinach oraz bardzo dobra opieka nad nimi są
        //           najlepszym modelem konsultingu IT.\n\nJesteśmy indywidualnym agentem naszych konsultantów.
        //           Promujemy ich kompetencje u naszych klientów, oferując możliwość uczestniczenia w
        //           różnych projektach, dołączając do tego finansową przejrzystość, wsparcie w rozwoju
        //           zawodowym i zawodową stabilność. Naszym głównym założeniem jest długoterminowość współpracy.
        //           Duża część zespołu 7N to osoby pracujące dla nas od wielu lat.`,
        //                     name: "7n",
        //                     www: "https://7n.com/",
        //                     type: "silver",
        //                     logo: require("../assets/partners/7N.svg")
        //                 },
        //                 {
        //                     id: "volvo",
        //                     description: `Volvo Group IT we Wrocławiu jest jednym z dwóch (obok Bangalore w Indiach)
        //           Globalnych Centrów IT w Grupie Volvo i odpowiada za tworzenie, rozwój i utrzymanie rozwiązań
        //           informatycznych dla koncernu Volvo. Przez lata zbudowaliśmy silny zespół ekspertów IT z zakresu
        //           architektury systemów IT, analizy biznesowej, programowania aplikacji, zarządzania projektami i
        //           testami. Ważną częścią Volvo Group IT we Wrocławiu jest dział metod i narzędzi, który opracowuje
        //           metodyki i modele stosowane w usługach dostarczanych w oparciu o światowe standardy, np. Devops,
        //           ITIL, Agile, Lean oraz o rozwiązania takie, jak Software Defect Prediction, które wyróżnia Volvo
        //           na rynku IT.\n\nDołącz do nas: [www.volvogroup.pl/kariera](www.volvogroup.pl/kariera)`,
        //                     name: "Volvo Group",
        //                     www: "https://www.volvogroup.pl/kariera",
        //                     type: "silver",
        //                     logo: require("../assets/partners/volvo.svg")
        //                 },
        //                 {
        //                     id: "dynatrace",
        //                     description: `**WHO WE ARE?**
        //
        // We thrive in a culture of innovation, with bytes running through our veins and code filling our brains. We deliver smart solutions that beat market standards by light years.
        //
        // For 8 years running, we have been and continue to be the leaders in the Application Performance Market because we create and develop an intelligent solution that enables thousands of software companies to deliver perfect user experience.
        //
        // “The software you build is only as good as the people who make it, we made a concerted effort that we must attract and retain the best developers in the world. It helps that our culture is unique, and our people feel as though they are part of creating something that isn’t evolutionary – it’s revolutionary.”
        //
        // John Van Siclen
        // Chief Executive Officer
        //
        // **WHAT WE DO?**
        //
        // We believe that thanks to us, the world changes for better, as we enable the great software people use daily around the world to work perfectly.
        //
        // At Dynatrace Gdansk Lab, we design, create and develop a best-in-class product that provides full insight into the performance of software products and IT systems. We continuously improve and deliver a solution that enables companies to scale up and make bold decisions concerning their future. Our Dynatrace solution monitors the full stack –from the network, through the database and application tiers, into the code-specific analysis, and all the way out to end-user devices and third-party add-ins. The same holds true for in-house hosted applications and enterprise clouds, public clouds, and hybrid clouds where containerization, hyper-scale, and elastic computing dominate. All of this is automated, not only the initial setup and instrumentation, but also problem identification, dashboards, ongoing adaptation to environmental and application changes, and upgrading the Dynatrace platform itself. We create Dynatrace because the world needs software to work perfectly.`,
        //                     name: "Dynatrace",
        //                     www: "https://jobs.dynatrace.pl/",
        //                     type: "silver",
        //                     logo: require("../assets/partners/dynatrace.svg")
        //                 },
        //                 {
        //                     id: "eworkgroup",
        //                     description: `Ework Group jest wiodącą siecią niezależnych konsultantów i liderem na rynku skandynawskim. Działamy w sektorach IT, telekomunikacji, nowych technologii oraz rozwoju biznesu. Współpracujemy z ponad 9000 konsultantów świadczących usługi dla ponad 170 klientów.
        //
        // Akcje Ework są notowane na giełdzie NASDAQ OMX w Sztokholmie. W Polsce istniejemy od 2015 roku przekształcając oblicze rodzimej branży IT.`,
        //                     name: "Ework group",
        //                     www: "https://www.eworkgroup.com/pl",
        //                     type: "silver",
        //                     logo: require("../assets/partners/ework_group.svg"),
        //                     orientation: "horizontal"
        //                 },
        //                 {
        //                     id: "nfj",
        //                     description: `[No Fluff Jobs](https://nofluffjobs.com/) to najpopularniejszy portal z ogłoszeniami IT w Polsce. U nas każde ogłoszenie jest aktualne, zawiera widełki płacowe, a także wszystkie szczegóły na temat danego stanowiska, przedstawione w prosty i czytelny sposób. Już od 3 lat zmieniamy standardy rekrutacji IT w naszym kraju, przekonując pracodawców do transparentności i uczciwości wobec potencjalnych pracowników.`,
        //                     name: "No Fluff Jobs",
        //                     www: "https://www.nofluffjobs.com ",
        //                     type: "silver",
        //                     logo: require("../assets/partners/nfj.svg")
        //                 },
        //                 {
        //                     id: "viacom",
        //                     description: `As a part of leading global entertainment company Viacom.tech offers challenging projects for iconic brands like MTV, Nickelodeon, Nick Jr, COMEDY CENTRAL, and Paramount Channel. Focusing on Viacom’s global engineering organization, the Warsaw Software Development Center plays a crucial role in the creation of highly efficient, advanced systems which are implemented by millions of users around the world. Our excellence center in Warsaw builds scalable and efficient systems, serving high volume traffic. We are an Agile and growing team of software engineers who use modern development and deployment technologies (Java 11, Kotlin, Swift, TypeScript, Docker, Kubernetes, clouds).
        //
        // Viacom.Tech is pioneering a new era of data in entertainment with opportunities in mobile application development, artificial intelligence, and data science. Want to become an architect of change and join a team of innovative thought leaders who aim to transform the entertainment industry with technological expertise? Here you can check out our current job openings and learn more about the company: https://viacom.tech/`,
        //                     name: "Viacom.Tech",
        //                     www: "https://viacom.tech/",
        //                     type: "gold",
        //                     logo: require("../assets/partners/viacom.svg"),
        //                     orientation: "horizontal"
        //                 },

        //                 {
        //                     id: "applause",
        //                     name: "Applause",
        //                     description: `Applause is the worldwide leader in crowdtesting and digital quality. Software is at the heart of how all brands engage users, and digital experiences must work flawlessly everywhere. With highly-vetted testers available on-demand around the globe, Applause provides brands with a full suite of testing and feedback capabilities. This approach drastically improves testing coverage, eliminates the limitations of offshoring and traditional QA labs, and speeds time-to-market for websites, mobile apps, IoT, and in-store experiences.
        // Thousands of leading companies — including Ford, Fox, Google, and Dow Jones — rely on Applause as a best practice to deliver high-quality digital experiences that customers love.`,
        //                     www: "https://www.applause.com/join-our-team",
        //                     type: "silver",
        //                     logo: require("../assets/partners/applause.svg"),
        //                     orientation: "horizontal"
        //                 },
        //                 {
        //                     id: "britenet",
        //                     name: "Britenet",
        //                     description: `Britenet to software house realizujący projekty dla międzynarodowych klientów. Firma działa na rynku od 13 lat, zatrudnia obecnie ponad 500 pracowników i ma biura w Warszawie, Poznaniu, Lublinie, Kielcach oraz Białymstoku.
        // Rozwój zawodowy idzie w parze z dobrą atmosferą, o czym świadczy tytuł Najlepsze Miejsce Pracy IT w Polsce zdobyty przez firmę aż cztery razy w badaniach Computerworld - TNS.`,
        //                     www: "https://britenet.com.pl",
        //                     type: "gold",
        //                     logo: require("../assets/partners/britenet.svg"),
        //                     orientation: "horizontal"
        //                 },
        //                 {
        //                     id: "j-labs",
        //                     name: "j-labs",
        //                     description: `We are polish software company founded by engineers for engineers in 2008 with all the best business practices and values.
        // We provide complete development teams, individual engineers and outsourcing of whole project in agile models in the field of software development.
        // Over 250 engineers work in our branches in Krakow and Warsaw. We support companies in the various sectors - such as banking, aviation, telco, as well as niches like machine learning, woodwork, and IoT.
        // Our activities based on technology, content, and reliability have been recognized by the Forbes Diamond 2017, and the distinction in the Business Gazelle 2018 ranking.
        //
        // Join us:  [https://www.j-labs.pl/open-positions/](https://www.j-labs.pl/open-positions/)`,
        //                     www: "https://www.j-labs.pl",
        //                     type: "silver",
        //                     logo: require("../assets/partners/jlabs.svg"),
        //                     orientation: "horizontal"
        //                 },
        //                 {
        //                     id: "decerto",
        //                     name: "decerto",
        //                     description: `#### W skrócie o nas:
        //
        // Decerto to polska firma informatyczna, specjalizująca się w budowie i wdrażaniu systemów dla biznesu. Na wysoką pozycję wśród dostawców IT składa się 12 letnie doświadczenie we współpracy z wiodącymi firmami na rynkach ubezpieczeniowym, finansowym i medialnym oraz wysoko wykwalifikowana kadra.
        // Posiadamy autorskie rozwiązania oraz tworzymy systemy dostosowane do indywidualnych potrzeb Klientów. Preferujemy długą i stabilną współpracę opartą na spełnianiu oczekiwań, zaufaniu i odpowiedniej kulturze pracy.
        //
        // #### Kultura  pracy:
        //
        // Największy nacisk kładziemy na odpowiednie kwalifikacje swoich współpracowników oraz ich satysfakcję z pracy. Poprzez pracę z najlepszymi oraz pozytywną atmosferę stwarzamy warunki do kreatywnej pracy i osiągania sukcesów.
        // Nasz zespół liczy ok. 140 osób. Stawiamy na przyjazną atmosferę, wzajemny szacunek i pomoc. Cechuje nas nastawienie na cel, aktywne szukanie rozwiązań i elastyczne podejście do zagadnień.
        // Praca to znacząca część naszego życia, dlatego dbamy o to, by w firmie wszyscy czuli się komfortowo. Lubimy też spotkać się po godzinach pracy ☺
        //
        // #### Nasza oferta:
        //
        // - Możliwość wyboru przydziału projektowego – różnorodne projekty, różne lokalizacje
        // - Wewnętrzne szkolenia oraz TechFlashe
        // - Dodatkowy, roczny budżet na rozwój
        // - Dofinansowanie certyfikatów
        // - Dowolny model współpracy
        // - Pakiet benefitów
        // - Wyjazdy i spotkania integracyjne
        //
        // Stawiamy na wzajemną uczciwość, otwartość i zaufanie. Jesteśmy elastyczni wobec potrzeb naszych współpracowników i tego samego oczekujemy.
        //
        // Poznaj nas i dołącz do zespołu:
        //
        // [www.decerto.pl/kariera](http://www.decerto.pl/kariera)
        // [www.facebook.com/FirmaDecerto](https://www.facebook.com/FirmaDecerto )
        // [www.instagram.com/firmadecerto/](http://www.instagram.com/firmadecerto/)
        // `,
        //                     www: "https://decerto.pl/kariera.html",
        //                     type: "silver",
        //                     logo: require("../assets/partners/decerto.svg"),
        //                     orientation: "horizontal"
        //                 },
        //                 {
        //                     id: "roche",
        //                     name: "roche",
        //                     description: `Founded in Switzerland in 1896, [Roche](https://it.roche.pl/) has helped millions of patients around the world with innovative pharmaceuticals and diagnostics. We have been at the forefront of cancer research and treatment for over 50 years. We invest around 9 billion Swiss francs in R&D every year because innovation is our lifeblood. We need advanced IT solutions to perform research, development, production and distribution of new medicines.
        //
        // Roche Global IT Solution Centre is based in Poland, in two locations: Warsaw and Poznan, and uses the cutting-edge technologies to improve healthcare. We are more than 500 people who create solutions that help doctors, patients and scientist around the world.`,
        //                     www: "https://it.roche.pl",
        //                     type: "gold",
        //                     logo: require("../assets/partners/roche.svg")
        //                 },
        //                 {
        //                     id: "4finance",
        //                     name: "4finance",
        //                     description: `
        // We are one of Europe’s largest digital consumer lending groups. We get money to people when they need it – quickly, conveniently and responsibly.
        // We use cutting edge technology and data to offer fast and convenient loans to customers across 16 countries.
        //
        // Since we were established in 2008, we have provided more than 15,000,000 Single Payment, Line of Credit and Instalment Loans totalling over EUR 6.0 billion.
        //
        // 4finance IT is part of 4finance Group. We focus on bringing the right mix of technologies to deliver a great service for our customers, making sure our customers receive a simple and fast service. Our IT teams work across Europe to develop global products and support local initiatives with skill and insight.
        //
        // [https://www.4financeit.com/](https://www.4financeit.com/)
        // [https://www.4finance.com](https://www.4finance.com)
        // [https://www.4finance.com/jobs](https://www.4finance.com/jobs)  `,
        //                     www: "https://www.4financeit.com/",
        //                     type: "silver",
        //                     orientation: "horizontal",
        //                     logo: require("../assets/partners/4finance.svg")
        //                 },
        //                 {
        //                     id: "evojam",
        //                     name: "evojam",
        //                     description: `Evojam is an unique blend of flexibility & quality. Strong technology with lightweight approach. Small team who can deliver difficult projects.
        // Our motto is KEEP GROWING. That's why Evojam's core values are learning and doing good quality work. This means we use every opportunity to learn, and we know how important teamwork is in growing and learning.
        // We build applications that work, that scale, that can be maintained over a longer time. We use Scrum and modern tech-stack: Scala, Java8+, NodeJS, Angular, TypeScript, React, MongoDB and plenty more.
        // Our primary customers are startups in the scaling phase from around the globe.
        //
        // For growth, fun & building stuff that works, join Evojam!`,
        //                     www: "https://www.evojam.com",
        //                     type: "silver",
        //                     orientation: "horizontal",
        //                     logo: require("../assets/partners/evojam.svg")
        //                 },
        //                 {
        //                     id: "nbc",
        //                     name: "NBC",
        //                     description: `Robimy projekty. Budujemy zespoły.
        // Tworzenie oprogramowania i ciągłe szukanie innowacyjnych rozwiązań to coś co nas nakręca i motywuje do działania.
        // Pomagamy klientom z branży finansów, bankowości, mediów i nowych technologii poprzez m.in. rozbudowę zespołów deweloperskich, utrzymanie oprogramowania czy też rozwój systemów.
        //
        // Co nas cechuje?
        // * pracujemy z grupą najlepszych programistów, projektantów i specjalistów IT w Polsce.
        // * dbamy o to by szybko i rzetelnie dobierać osoby odpowiedzialne za projekt IT
        // * łączymy pasję z profesjonalizmem.
        // * w doborze i selekcji pracowników kierujemy się doświadczeniem. Aż 40 % naszego zespołu stanowią eksperci, kolejne 45% to wysoko wykwalifikowani specjaliści.
        // * realizujemy innowacyjne i różnorodne projekty. Uwzględniając wszystkie preferencje oferujemy elastyczną formę zatrudnienia i wybór świadczeń. Prace zdalną w naszym biurze lub bezpośrednio u klienta.
        //
        // Do współpracy zapraszamy:
        // * programistów,
        // * analityków,
        // * administratorów,
        // * projektantów,
        // * kierowników projektów,
        // * konsultantów,
        // * testerów oraz IT managerów
        //
        // Satysfakcja ludzi, z którymi pracujemy, jest dla nas kluczowa. Dlatego indywidualnie podchodzimy zarówno do swoich partnerów biznesowych, jak i do naszych pracowników. Chcemy zrozumieć Twoje cele oraz plany i pomóc Ci w ich osiągnięciu.
        // Jesteśmy w całości samofinansującą się polską firmą, która wszystkie swoje działania opiera na konkretnych wartościach przyświecających nam od początku istnienia. Dzięki konsekwencji i niesamowitemu zespołowi zdobyliśmy uznanie w wielu prestiżowych rankingach, takich jak: Gazele Biznesu, ComputerWorld Top200 i IT@Bank.
        // Adresy naszych biur:
        // * Wrocław - siedziba główna NBC - ul. Grabiszyńska 251a.
        // * Warszawa - Chmielna Business Center, ul. Chmielna 132/ 134.
        // * Gdańsk - Tryton Business House, ul. Jana z Kolna 11
        // * Kraków - ul. Basztowa 3/16.
        // * Katowice - ul. Zabrska 16/3
        //
        // Zapraszamy na naszą stronę: [www.nbc.com.pl](https://www.nbc.com.pl)`,
        //                     www: "https://www.nbc.com.pl",
        //                     type: "silver",
        //                     logo: require("../assets/partners/nbc.svg")
        //                 },
        //                 {
        //                     id: "alior",
        //                     name: "Alior Bank",
        //                     description: `[Alior Bank](https://www.aliorbank.pl/dodatkowe-informacje/kariera/praca-w-it.html) jest jednym z najdynamiczniej rozwijających się banków uniwersalnych, świadczącym pełen zakres usług skierowanych do klientów detalicznych i biznesowych.
        // Bank systematycznie umacnia pozycję rynkową, skutecznie łącząc zasady tradycyjnej bankowości z innowacyjnymi rozwiązaniami i produktami. O najwyższą jakość produktów i usług Banku dba ponad 8000 pracowników. W 2017r. została ogłoszona strategia banku na lata 2017-2020 pod hasłem "Cyfrowy buntownik", co oznacza technologiczną transformację każdego obszaru działania banku. W ramach przyjętej strategii są już realizowane projekty w obszarze:
        //
        // automatyzacji
        // robotyzacji
        // blockchain
        // chmura
        // PSD2/API
        // sztuczna inteligencja DRONN
        // biometria
        // Alior Bank jest jedną z najbardziej rozpoznawalnych marek na polskim rynku bankowym. Ikoną Banku, która łączy świat tradycyjnej bankowości i nowatorskich rozwiązań jest postać Bankiera w meloniku. Dodatkowo podkreśla to hasło „WYŻSZA KULTURA. BANK NOWOŚCI.”.
        //
        // Dzięki bliskiej współpracy pomiędzy działami, szybkości działania, skracaniu dystansu między szeregowymi pracownikami a zarządem, mamy poczucie, że pracujemy w wyjątkowej firmie. To miejsce dla ludzi, którzy mają pomysły i odwagę biznesową, by wyznaczać nowe standardy bankowości.
        //
        // Od 2014 r. akcje Alior Banku wchodzą w skład indeksu WIG20 skupiającego największe i najbardziej płynne spółki notowane na Giełdzie Papierów Wartościowych w Warszawie.
        //
        // Alior Bank jest wielokrotnym laureatem prestiżowych nagród oraz zwycięzcą rankingów o zasięgu międzynarodowym. Ważniejsze osiągnięcia z 2018 roku:
        //
        //  1\\. miejsce w rankingu „Przyjazny Bank Newsweeka 2018” w kategorii „Bankowość internetowa”,
        // 1\\. miejsce w kategorii „New Digital Venture” w konkursie The Heart Corporate Innovation Awards 2018 za partnerstwo Alior Banku i cyfrowej platformy pośrednictwa finansowego online Bancovo,
        // tytuł Lidera Informatyki 2018 w 22. edycji konkursu organizowanego przez magazyn „Computerworld”. Głównymi kryteriami wyboru jest biznesowa wartość projektów dostarczanych przez IT oraz ich innowacyjność,
        // nagroda honorowa w kategorii „Banki innowacyjne” w rankingu 50. największych banków w Polsce 2018 roku.`,
        //                     www:
        //                         "https://www.aliorbank.pl/dodatkowe-informacje/kariera/praca-w-it.html",
        //                     type: "silver",
        //                     orientation: "horizontal",
        //                     logo: require("../assets/partners/alior.svg")
        //                 },
        //                 {
        //                     id: "coi",
        //                     name: "Centralny Ośrodek Informatyki",
        //                     description: `Są tacy, dla których przy tworzeniu usług cyfrowych najważniejszy jest człowiek. Jest miejsce, gdzie dzięki technologii rozproszone instytucje łączą się w proste usługi przyjazne dla obywateli – tak wygląda kod do cyfryzacji. W Centralnym Ośrodku Informatyki realizujemy projekty dla Ministerstwa Cyfryzacji.
        // Jesteśmy unikalnym w skali państwa zespołem liczącym ponad 500 specjalistów.
        // Zatrudniamy ekspertów IT
        // Zarządzamy Systemem Rejestrów Państwowych
        // Projektujemy e-usługi
        // Prowadzimy projekty
        // Dzielimy się wiedzą
        //
        // Mamy swoje biura w  Warszawie, Lublinie, Katowicach, Łodzi, Rzeszowie, Poznaniu, Bydgoszczy i Gorzowie Wielkopolskim.
        // \\#cyfryzacjatofrajda
        // My już jesteśmy częścią cyfryzacji, a Ty? Przetestuj [coi.gov.pl](https://www.coi.gov.pl/).`,
        //                     www: "https://www.coi.gov.pl/",
        //                     type: "silver",
        //                     orientation: "horizontal",
        //                     logo: require("../assets/partners/coi.svg")
        //                 },
        //                 {
        //                     id: "sml",
        //                     name: "SoftwareMill",
        //                     description: `SoftwareMill is a consulting & custom software development company, delivering services remotely, worldwide for 10 years. Being experts in Scala (Akka, Play, Spark), Java, Kotlin we specialize in distributed, big data systems, blockchain, machine learning, IoT, and data analytics.
        //
        // Our focus on quality, self-improvement and a true engineering approach results in systems that do their job, bring value to clients, help them scale and grow.`,
        //                     www: "https://softwaremill.com/",
        //                     type: "silver",
        //                     orientation: "horizontal",
        //                     logo: require("../assets/partners/sml.svg")
        //                 },
        //                 {
        //                     id: "netcompany",
        //                     name: "netcompany",
        //                     description: `Netcompany has gone from being a small start-up in 2000 to more than 2000 skilled IT consultants and developers located in offices in Poland, Denmark, Norway, the United Kingdom and Vietnam. Over 240 of our employees is located at our office in Warsaw. Currently we’re conducting over 160 various projects for the biggest private companies as well as for public sector entities from Denmark, Norway and United Kingdom – world’s leaders of information and communication technologies usage. At Netcompany we rely on one another and we develop quickly. We are team players
        //
        // IT people leading IT people
        // That is the core of Netcompany's decentralized management model. From top to bottom, our organization consists of IT professionals with extensive expertise. We understand that our employees are the key to maintaining our position as the most skilled and fastest growing IT consulting firm. That is why we have created a business model where we constantly focus on developing our employees to becoming the best in the industry. Currently we are looking for talented Java Developers for our Polish office that will help us achieve our goals and continuous growth. You can become a part of exciting projects working closely together with your Polish, Danish and Norwegian colleagues.`,
        //                     www: "https://www.netcompany.com/pl",
        //                     type: "gold",
        //                     orientation: "horizontal",
        //                     logo: require("../assets/partners/netcompany.svg")
        //                 },
        //                 {
        //                     id: "sages",
        //                     name: "Sages",
        //                     description: `Sages sp. z o.o. jest firmą szkoleniową i doradczą działającą od 10 lat w branży IT. Misją firmy jest wspieranie procesu projektowania, wytwarzania i wdrażania oprogramowania w przedsiębiorstwach i instytucjach publicznych, aby zminimalizować koszty, czas i ryzyko, związane z tymi przedsięwzięciami. Naszym celem jest łączenie wiedzy pochodzącej ze środowisk akademickich i doświadczenia ekspertów z obszaru praktycznych zastosowań w biznesie, aby budować kompetencje kadr w Polsce, a w szczególności przyczynić się do wzrostu efektywności i innowacyjności krajowej branży IT.
        //
        // Specjalizuje się w prowadzeniu szkoleń i kursów technologicznych, których cechą wspólną jest warsztatowa formuła zajęć koncentrująca się na przekazaniu praktycznych umiejętności uczestnikom. Współpracuje także z uczelniami wyższymi wspierając realizację zaawansowanych przedmiotów programistycznych, a także prowadząc studia podyplomowe (Politechnika Warszawska, Akademia Leona Koźmińskiego w Warszawie). Ponadto jest stałym organizatorem eventów branżowych dla profesjonalistów IT w ramach inicjatywy Stacja IT.
        //
        // Podczas tegorocznej edycji Confitury firma Sages jest oficjalnym opiekunem ścieżki Big Data & AI.`,
        //                     www: "http://www.sages.com.pl",
        //                     type: "path",
        //                     orientation: "horizontal",
        //                     logo: require("../assets/partners/sages.svg")
        //                 },
        //                 {
        //                     id: "circleK",
        //                     name: "Circle K",
        //                     description: `You´ve probably already met us. Maybe you´ve stopped by for a coffee, fueled up your car or grabbed something to eat on the go. Then you know what Circle K is all about. Making everyday life easier for people all over the world.
        //
        // We´ve grown into a successful global company with over 15.000 stores in 24 countries, serving more than 6 million customers each day. In all, we have more than 120.000 people working at our stores and support offices.
        //
        // In Circle K Business Centre, in Warszawa, which is part of our SSC structures, we provide IT services for Circle K in Europe, US and Canada. We have different functions here: Service Desk, Infrastructure, Network Operations, Development but what we have in common is the fact that no matter what we do, we understand the world we live in. We can see how fast it changes and how fascinating this can be. Curiosity and openness drives us towards new ideas, innovations, new technologies.
        //
        // Circle K is growing a team full of passionate, open-minded people with creative energy to handle our custom built products in the digital area.`,
        //                     www: "https://business-centre-europe.circlek.com/",
        //                     type: "silver",
        //                     orientation: "horizontal",
        //                     logo: require("../assets/partners/circleK.svg")
        //                 },
        //                 {
        //                     id: "spartez",
        //                     name: "Spartez",
        //                     description: `We at Spartez, are known for our experts, our partnership with Atlassian and our world-class products. We are growing exponentially and looking for talented, experienced and passionate professionals to join our team.
        //
        // If you are someone or you know someone who loves to take on challenges, work independently and make an impact, then read on!
        //
        // Spartez is a Gold Solution Partner of Atlassian. Our world-class products include Agile Cards for Jira, Agile Poker for Jira, Canned Responses Pro for Jira, Asset Tracker for Jira and TFS4JIRA amongst others. We are a truly international team with over 170 employees from over 11 countries who live and work mostly out of Gdansk. We believe that happy Spartans means happy customers and thus offer some of the best employee benefits and facilities in the region. We have some of the brightest minds working for us, an awesome office and a very low attrition rate. Like we said, once you are a Spartan, you never want to be anything else.
        //
        // Our company is deeply rooted in a few important values and principles - just a few, simple and meaningful rules:
        //
        // \\# Don't Fuck The Customer
        //
        // \\# Be The Change You Seek
        //
        // \\# Open Company - No Bullshit
        //
        // \\# Great Place to Work
        //
        // \\# Simplicity`,
        //                     www: "https://spartez.com/",
        //                     type: "silver",
        //                     orientation: "horizontal",
        //                     logo: require("../assets/partners/spartez.svg")
        //                 },
        //                 {
        //                     id: "goldman-sachs",
        //                     name: "Goldman Sachs",
        //                     description: `Working together, we see the potential in the world to create more. To turn big ideas into realities. To challenge ourselves to look ahead and make things possible. From automated trading to managing data, risk analysis to safeguarding information and promoting environmental responsibility, our commitment to best-in-class technology empowers everything we do.
        //
        // The Goldman Sachs Group, Inc. is a leading global investment banking, securities and investment management firm that provides a wide range of financial services to a substantial and diversified client base that includes corporations, financial institutions, governments and individuals. Founded in 1869, the firm is headquartered in New York and maintains offices in all major financial centers around the world.
        //
        // Make things possible at [goldmansachs.com/careers](https://www.goldmansachs.com/careers/)`,
        //                     www: "https://www.goldmansachs.com/careers/",
        //                     type: "gold",
        //                     orientation: "horizontal",
        //                     logo: require("../assets/partners/gs.svg")
        //                 },
        //                 {
        //                     id: "rtb-house",
        //                     name: "RTB House",
        //                     description: `[RTB House](https://www.rtbhouse.com/) is a global company that provides state-of-the-art retargeting technology for top brands worldwide. Its proprietary ad buying engine is the first and only in the world to be powered entirely by deep learning algorithms, enabling advertisers to generate outstanding results and reach their short, mid and long-term goals.
        //
        // Founded in 2012, RTB House serves over a thousand campaigns across EMEA, APAC and the Americas regions with main locations in New York, London, Tokyo, Singapore, São Paulo, Moscow, Istanbul, Dubai and Warsaw.`,
        //                     www: "https://www.rtbhouse.com/",
        //                     type: "gold",
        //                     orientation: "horizontal",
        //                     logo: require("../assets/partners/rtb.svg")
        //                 },
        //                 {
        //                     id: "it-kontrakt",
        //                     name: "IT Kontrakt",
        //                     description: `IT Kontrakt powstał w 2004 jako jedna z pierwszych w Polsce spółek wdrażających rozwiązania i usługi IT dla biznesu oraz kompleksowe wsparcie w budowaniu systemów informatycznych. Firma jest liderem w obszarze Managed Services, Nearshore/Offshore, Information Technology Outsourcing. Posiada siedem oddziałów w Polsce, a także oddział w Kuala Lumpur w Malezji, biuro handlowe w USA oraz własne Software Delivery Centers w pięciu lokalizacjach. Do portfolio klientów należą firmy głównie z branż: farmacja, automotive, finanse, IT i telekomunikacja.
        //
        // IT Kontrakt, w ramach inwestycji Oaktree Capital Management i Cornerstone Partners, realizuje budowę „IT Services Competence Platform”. Projekt opiera się na intensywnym programie Mergers & Acquisitions i szybkim wzroście organicznym spółek. Celem inwestycji jest stworzenie globalnej organizacji oferującej kompetencje, ekspertyzę i rozwiązania softwarowe dla korporacji międzynarodowych oraz klientów ze Skandynawii, krajów regionu DACH, UK i USA, i ich obsługa przez własne centra delivery zlokalizowane w krajach regionu CEE oraz Azji. Zatrudnienie w Grupie sięga blisko 2.400 inżynierów.`,
        //                     www: "https://www.itkontrakt.com/",
        //                     type: "silver",
        //                     logo: require("../assets/partners/it-kontrakt.svg")
        //                 },
        //                 {
        //                     id: "fis",
        //                     name: "FIS",
        //                     description: `FIS is the world’s largest provider of banking and payments technology solutions and a global leader in consulting and outsourcing solutions. With a long history deeply rooted in the financial services sector, FIS serves more than 14,000 institutions in over 130 countries. Headquartered in Jacksonville, Fla., FIS employs more than 55,000 people worldwide and holds leadership positions in payment processing and banking solutions, providing software, services and outsourcing of the technology that drives financial institutions. In October 2014 FIS completed acquisition of Brussels-based Clear2Pay. The transaction brings new corporate payment solutions and services, inclusive of high-value and cross-currency corporate payments, payments managed services, and payments processing utilities that will further bolster FIS’ payments portfolio across all geographies. Clear2Pay is now an FIS company.`,
        //                     www: "https://www.fisglobal.com/",
        //                     type: "gold",
        //                     logo: require("../assets/partners/fis.svg"),
        //                     orientation: "horizontal"
        //                 },
        //                 {
        //                     id: "sollers",
        //                     name: "Sollers Consulting",
        //                     description: `Sollers Consulting is an international business advisory and software implementation specialist supporting the financial industry in business transformations. Sollers Consulting Teams have supported over 70 financial groups in enhancing their digital capabilities. Among the companies partnering with Sollers Consulting are Allianz, Axa, LV=, BNP Paribas Cardif, Basler, Generali, Zurich, Santander Consumer Bank, ING and many more.
        //
        // Sollers Consulting specialises in IT systems, helping insurers, banks and leasing companies to transform and adapt to new technologies. The company offers RIFE, a digital platform designed for the needs of the insurance industry. Sollers Consulting cooperates with more than 15 technology providers such as Guidewire Software, Tia Technology, Fadata, Oracle, AWS or Microsoft.
        //
        // About 600 business and IT specialists from Warsaw, Lublin, Poznan, Cologne, Tokyo and Copenhagen are helping financial institutions in Germany, Great Britain, Poland, Scandinavia and many other countries to reap the benefits of digitalisation.
        //
        // More information about career in Sollers Consulting at: [https://career.sollers.eu/](https://career.sollers.eu/)
        // `,
        //                     www: "https://career.sollers.eu/",
        //                     type: "silver",
        //                     logo: require("../assets/partners/sollers.svg"),
        //                     orientation: "horizontal"
        //                 },
        //                 {
        //                     id: "alten",
        //                     name: "Alten",
        //                     description: `Jesteśmy częścią międzynarodowej grupy ALTEN, skupiającej na całym świecie ponad 33 700 wybitnych specjalistów w dziedzinach inżynierii oraz technologii informatycznych. Realizujemy najbardziej innowacyjne projekty technologiczne dla naszych Partnerów biznesowych.
        //           Nasz zespół to profesjonaliści, którzy dzięki zaangażowaniu i znajomości potrzeb rynku sprawiają, że grupa ALTEN wciąż jest światowym liderem usług konsultingu inżynieryjno-technologicznego.`,
        //                     www: "https://www.altenpolska.pl",
        //                     type: "silver",
        //                     logo: require("../assets/partners/alten.svg")
        //                 },
        //                 {
        //                     id: "isolutions",
        //                     name: "Isolutions",
        //                     description: `Jesteśmy firmą, dla której punktem odniesienia w budowaniu pozycji rynkowej są ludzie. W praktyce oznacza to nacisk na partnerskie relacje, zaufanie, odpowiedzialność i leżący w centrum naszego DNA rozwój. Dzięki takiemu podejściu możemy oferować naszym Klientom wysokiej jakości usługi realizowane przez zgrane, zmotywowane i czerpiące przyjemność ze swojej pracy zespoły.
        //
        // W pracy stawiamy na nowe technologie, dobre praktyki, konstruktywny feedback, zwinne metodyki pracy i sprawną komunikację, co pozwala nam efektywnie wspierać naszych Klientów w realizacji wielu wyzwań, które pojawiają się wraz ze zmieniającym się rynkiem.
        //
        // Do każdego pracownika podchodzimy indywidualnie, dbając o autonomię i poczucie wpływu:
        //
        // Mamy w pełni elastyczny czas pracy oraz możliwość pracy zdalnej zgodnie z potrzebami.
        //
        // Inwestujemy w rozwój – rozwijamy własny Startup Processor oraz zespół R&D, dzięki czemu rozwijamy własne produkty i oferujemy m.in. rozwiązania typu Proof of Concept, czy Minimum Viable Product.
        //
        // Dzięki naszemu autorskiemu rozwiązaniu - Forum Projektów – nasi programiści mają ogromny wpływ na to, w którym projekcie zostaną zaalokowani.
        //
        // Dzięki naszemu autorskiemu rozwiązaniu – Teamtoolbox na bieżąco śledzimy poziom zadowolenia z pracy w naszej firmie, ze współpracy z naszymi klientami oraz z realizacji zadań, dzięki czemu sprawnie reagujemy na potrzeby naszych pracowników.
        //
        // Mamy dedykowany zespół, który dba o komfort naszych ludzi od pierwszego dnia pracy, bez względu na to, czy pracują w siedzibie firmy, czy w siedzibach naszych Klientów.
        //
        // Dzielimy się wiedzą, na zewnętrznych meetupach i wewnętrznych szkoleniach. Mamy też budżet rozwojowy pozwalający rozwijać kompetencje zgodnie z zainteresowaniami i potrzebami projektowymi.
        //
        // Mamy płaską strukturę. Każdy zespół organizuje się zgodnie ze swoją specyfiką i potrzebami projektowymi.
        //
        // Nie prowadzimy ocen rocznych, dbamy o przekazywanie feedbacku w trybie ciągłym.
        //
        // Nie uzależniamy awansów od stażu pracy, ale od kompetencji. Awansować w Isolution można w każdej chwili, po spełnieniu ustalonych z liderem warunków, indywidualnie dopasowanych do każdej osoby.
        //
        // Cenimy sobie ponadto transparentność, wzajemne docenienie, współpracę, ale także wspólną, dobrą zabawę.
        //
        // Jesteśmy dumni nie tylko z tego, co robimy, ale również z tego kim jesteśmy i jaką kulturę budujemy, co przekłada się również na atmosferę pracy oraz ambitne cele, które przy takim zespole możemy sobie stawiać.`,
        //                     www: "https://isolution.pl/kariera/",
        //                     type: "silver",
        //                     logo: require("../assets/partners/is.png"),
        //                     orientation: "horizontal"
        //                 },
        //                 {
        //                     id: "pentacomp",
        //                     name: "Pentacomp",
        //                     description: `Historia Pentacomp Systemy Informatyczne S.A. zaczęła się 24 lata temu od pięciu komputerów, właścicieli których połączyła wspólna pasja – programowanie i marzenie o rzeczach większych, niż te, które można osiągnąć w pojedynkę. Obecnie, Pentacomp to ponad 250 „zwinnych" specjalistów. To oni wprowadzają cyfrową rewolucję w administracji oraz pomagają skutecznie, szybko i innowacyjnie rozwijać biznes prywatnych przedsiębiorstw.
        // `,
        //                     www: "https://www.pentacomp.pl/",
        //                     type: "bronze",
        //                     logo: require("../assets/partners/pentacomp.svg"),
        //                     orientation: "horizontal"
        //                 },
        //                 {
        //                     id: "ppl",
        //                     name: `'Polish Airports' State Enterprise`,
        //                     description: `'Polish Airports' State Enterprise (PPL) is one of the top aviation infrastructure companies in Poland, taking active part in the shaping and development of this strategic branch of the industry. PPL resources include Warsaw Chopin Airport (EPWA) – the biggest Polish airport and one of the biggest airports in Central and Eastern Europe.`,
        //                     www: "https://www.polish-airports.com/",
        //                     type: "silver",
        //                     logo: require("../assets/partners/ppl.svg"),
        //                     orientation: "horizontal"
        //                 },
        //                 {
        //                     id: "atena",
        //                     name: "Atena",
        //                     description: `Atena IT & Financial Services Inc. designs, implements and integrates comprehensive IT systems for businesses. We specialise in developing products for the insurance market. For years, Atena has been the leading supplier of systems for this industry. We develop custom turnkey solutions for back office, front office, middleware, and business intelligence.`,
        //                     www: "https://www.atena.pl",
        //                     type: "gold",
        //                     logo: require("../assets/partners/atena.svg"),
        //                     orientation: "horizontal"
        //                 },
        //                 {
        //                     id: "groupon",
        //                     name: "GROUPON",
        //                     description: `Groupon (NASDAQ: GRPN) jest globalnym liderem w dziedzinie handlu lokalnego i miejscem od którego warto zacząć, jeśli chcesz dokonać niemal dowolnego zakupu, w dowolnym czasie, gdziekolwiek jesteś. Dzięki wykorzystaniu globalnych kontaktów firmy i skali jej działalności, Groupon oferuje konsumentom ogromny wybór najlepszych okazji z całego świata. Klienci odnajdują to, co najlepsze ma do zaoferowania ich miasto, w sieci lub w telefonie komórkowym poprzez lokalne oferty Groupon, mogą wyjechać na wakacje poprzez Groupon Getaways i wybierać spośród oferty pieczołowicie dobranego sprzętu elektronicznego, ubrań, artykułów gospodarstwa domowego i wielu innych produktów poprzez Groupon Goods.
        //
        // Groupon zmienia to, w jaki sposób tradycyjne niewielkie biznesy mogą zdobywać, utrzymywać i nawiązywać kontakt z klientami, dostarczając swoim kontrahentom wybór produktów i usług, w tym niestandardowe kampanie marketingowe, które pomagają biznesom w rozwoju oraz w prowadzeniu skutecznej działalności. Naszym celem jest połączenie lokalnych przedsiębiorców i zwiększającej się siły nabywczej konsumentów, napędzając biznes lokalnym kupcom poprzez cenę oraz pokazanie ich ofert.`,
        //                     www: "https://people.groupon.com/poland",
        //                     type: "silver",
        //                     logo: require("../assets/partners/groupon.svg"),
        //                     orientation: "horizontal"
        //                 },
        //                 {
        //                     id: "prodata",
        //                     name: "ProData Consult",
        //                     description: `We are one of the largest international consulting company in Northern Europe with offices in Denmark, Sweden, Norway, Germany, the Netherlands and Poland. We work with the best specialists vastly experienced  in IT sector. We carry out challenging projects across a number of industries – accept military. Our relationship with Consultants is built on an open and honest communication.
        //
        // We act within the Scandinavian business culture focused on achieving goals while maintaining the balance between private and professional life. We offer attractive hourly rates and transparent pricing and professional stability (long term cooperation).`,
        //                     www: "https://www.itconsultants.pl/",
        //                     type: "silver",
        //                     logo: require("../assets/partners/prodata.png"),
        //                     orientation: "horizontal"
        //                 },
        //                 {
        //                     id: "luxoft",
        //                     name: "CGI",
        //                     description: `Luxoft, a DXC Technology Company, is a digital strategy and software engineering firm providing bespoke technology solutions that drive business change for customers globally. As part of DXC Technology (NYSE: DXC), Luxoft enables business transformation, enhances customer experiences, and boosts operational efficiency through its strategy, consulting, and engineering services.
        //
        // Luxoft combines a unique blend of engineering excellence and deep industry expertise to serve clients globally, specializing in automotive, financial services, travel and hospitality, healthcare, life sciences, media and telecommunications.
        //
        // We offer a full stack of digital expertise including edge computing, server-less computing, AI and Machine Learning, plus we leverage both open source and native cloud platforms and APIs (mainly Big Data, Information Security, Cloud, DevOps, Mobile, Blockchain and IoT). We work using various agile methodologies (Kanban, Scrum, Waterfall) and our experienced project teams design, implement, maintain, and develop our clients’ digital assets.
        //
        // We have a relaxed corporate culture that’s highlighted by the best employees this side of Jupiter who always make fun a priority. But don’t get us wrong, it’s not all fun and games. We take our work and coding seriously and our ever-expanding global reach offers you a whole new world of opportunities that help you to \\#GoFarwithLuxoft. Go to: [www.poland.luxoft.com](https://www.poland.luxoft.com)`,
        //                     www: "https://career.luxoft.com/",
        //                     type: "silver",
        //                     logo: require("../assets/partners/luxoft.svg"),
        //                     orientation: "horizontal"
        //                 },
        //                 {
        //                     id: "justjoinit",
        //                     name: "Just Join IT",
        //                     description: `With Just Join IT you can find a better job, closer than you think in a convenient way. We intend to focus on only the best software houses & startups. Join it and build with us the first map of the labor market in the IT sector of Poland.`,
        //                     www: "https://justjoin.it",
        //                     type: "media",
        //                     logo: require("../assets/partners/justjoinit.png"),
        //                     orientation: "horizontal"
        //                 },
        //                 {
        //                     id: "crossweb",
        //                     name: "Crossweb",
        //                     description: `W Crossweb.pl zbieramy informacje o odbywających się wydarzeniach związanych z szeroko rozumianą branżą internetową.
        // Serwis skierowany jest do osób zainteresowanych tematyką IT, Startup, UX, SEM/SEO oraz e-biznes, chcących brać udział w barcampach, spotkaniach, warsztatach, konferencjach czy targach pracy. Co ważne, serwis obejmuje nie tylko duże, wszystkim znane wydarzenia ale także te mniejsze spotkania, które często są równie ciekawe.
        // Nad każdym publikowanym wydarzeniem w Crossweb czuwa moderator, dbając o jakość każdego wpisu. Co ważne, nie tylko czekamy na zgłoszenia od organizatorów, ale także sami poszukujemy wydarzeń, dlatego warto sprawdzić, czy wydarzenie nie znajduje się już w serwisie.`,
        //                     www: "https://crossweb.pl",
        //                     type: "media",
        //                     logo: require("../assets/partners/crossweb.png"),
        //                     orientation: "box"
        //                 },
        //                 {
        //                     id: "codersview",
        //                     name: "Coders View Community",
        //                     description: `Coders View Community to społeczność ludzi pasjonujących się programowaniem. Gromadzi całe środowisko IT wokół ciekawych wydarzeń, szkoleń, warsztatów, meetupów i interesujących projektów, w których można wziąć udział. Poza specjalistami w dziedzinie różnych języków programowania poznacie tutaj również ludzi, którzy pomogą w znalezieniu ciekawej pracy z możliwością rozwoju i zgodnie z Waszymi  oczekiwaniami. CVC to nie tylko osoby z dużym doświadczeniem, ale również takie, które dopiero zaczynają swoją przygodę z programowaniem.
        // CVC jest częścią grupy GS Services League, która wspiera międzynarodowe organizacje w zakresie Outsourcingu IT, Rekrutacji Stałej oraz realizacji projektów w modelu Fixed-Price.
        // CVC to całe IT w jednym miejscu!`,
        //                     www: "https://codersview.eu",
        //                     type: "media",
        //                     logo: require("../assets/partners/codersview.svg"),
        //                     orientation: "horizontal"
        //                 },
        //                 {
        //                     id: "PROBrand",
        //                     name: "PROBrand",
        //                     description: `PRObrand: Brand New Event Technologies
        // Komfortowa obsługa techniczna targów, konferencji i kongresów w zakresie:
        // * Ekranów projekcyjnych, ekranów LED i telewizorów 43-98"
        // * Nagłośnienia
        // * Oświetlenia architektonicznego i scenicznego
        // * Osprzętu do tłumaczeń symultanicznych
        //
        // Wspieramy przygotowanie wydarzenia na wszystkich jego etapach.
        // Zapraszamy także do naszej wypożyczalni sprzętu eventowego: [www.eventav.pl ](https://www.eventav.pl )`,
        //                     www: "https://www.facebook.com/PRObrand-Sp-z-oo-170774782953644/",
        //                     type: "tech",
        //                     logo: require("../assets/partners/probrand.png"),
        //                     orientation: "horizontal"
        //                 }
      ];
    },
    // tslint:enable
    [LOAD_PARTNER_BY_ID]({ state, dispatch }, id: string) {
      return dispatch(LOAD_PARTNERS).then(() =>
        state.partners.find(
          partner => partner.id.toLowerCase() === id.toLowerCase()
        )
      );
    }
  }
};

function shuffle<T>(array: T[]): T[] {
  return array.sort(() => 0.5 - Math.random());
}

export interface PartnersState {
  partners: Partner[];
}
