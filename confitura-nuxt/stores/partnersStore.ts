import { defineStore } from 'pinia'

const partners = [
  {
    id: "jp-morgan",
    description: `**JPMorgan Chase & Co.** has expanded its consumer business and we have launched a new digital retail bank in the UK. We are offering consumers a completely new banking experience. Under the Chase brand, the bank provides products and features tailored to meet the needs of customers in the UK, made available via an innovative mobile app.

Our team is at the heart of building this new venture, focused on developing offerings that put the customer at the center. We have created a new organization and we are looking for solution-oriented, commercially minded, customer-focused engineers, used to working in a true agile environment who want to be a part of something new, built from the ground up as a green-field with zero legacy initiative within a global, diverse and inclusive team.

Culture is as important to us and we are looking for intellectually curious, new technology passionate individuals who would like to expand their skills whilst working on a new exciting venture for the firm. Your work will have a direct impact to our customers as our business expands around the world.

Want to help build the digital bank of the future? Join us!

At Chase, we know that people want great value combined with an excellent experience, from a bank they can trust. That’s why we’re revolutionising mobile banking, by creating seamless digital journeys that our customers love.

For us, that means keeping ourselves customer obsessed and always being open to trying new things. Above all, it’s about working with people who are passionate about building the bank of the future.

**Diversity and Inclusion**

We understand that everyone’s unique and that diversity of thought, experience and backgrounds is what makes a good team, great. By bringing people with different points of view together, we can represent everyone and truly reflect the communities we serve.

Who we’re looking for

Fintech is a fast-paced, ever-evolving world. So we like working with people who are adaptable, open to new things and don’t mind making mistakes. To help us tackle problems together, we also look for skills like critical thinking and problem solving, as well as having a curious mindset and being a great communicator.

So that you can get the most out of working with us, having a motivated, resourceful spirit is key, as well as a hunger to learn more about technology and financial services. If you’re a forward thinker with a head for fintech, we’d love to hear from you.

Check our open positions here:
 
https://www.chase.co.uk/gb/en/careers-at-chase/
`,
    name: "JPMorgan Chase & Co.",
    www: "https://www.chase.co.uk/gb/en/careers-at-chase/",
    type: "platinum",
    logo: ("/assets/partners/2023/Colour_Logo_Chase.svg"),
    orientation: "horizontal"
  },
  {
    id: "ing",
    description: `W ING rozwiązania jutra tworzymy już dzisiaj.
ING Bank Śląski to jeden z największych banków w Polsce z ofertą dla klientów indywidualnych, małych przedsiębiorców oraz dużych podmiotów gospodarczych.

Bank tworzą ludzie, którzy inspirują się do działania i wspierają wzajemnie, aby być krok do przodu, w życiu i w biznesie. Zatrudnia ponad 8 tysięcy pracowników w całej Polsce. To oni na co dzień dostarczają nowoczesne rozwiązania i narzędzia, dzięki którym bankowość staje się przyjazna i transparentna.

Indywidualny rozwój, ambitne zadania, dostęp do technologii i praca w zwinny sposób to tylko część oferty, którą ING proponuje kandydatom i zapewnia pracownikom.

Inspiruje do uczenia się i rozwoju na swój sposób, według własnego pomysłu i planu, zgodnie z zasadą brania odpowiedzialności za rozwój w swoje ręce.

Wartości ING wyrażają stosunek organizacji i pracowników do świata oraz do siebie nawzajem. Te wartości to między innymi: jesteśmy uczciwi, kierujemy się zdrowym rozsądkiem i jesteśmy odpowiedzialni.

Od wielu lat bank nagradzany jest tytułem Top Employer w Polsce. Oferuje pracownikom wiele rozwiązań, dba o komfort miejsca pracy i dobrą atmosferę.

To bank przyszłości zarówno dla klientów jak i dla pracowników.

Centrala znajduje się w Katowicach i Warszawie.
 
Pracuj z nami i rozwijaj się w ING Banku Śląskim. Rekrutujemy do:
- IT
- Ryzyka
- Bankowości korporacyjnej
- Bankowości detalicznej
- KYC/ AML
- Finansów
- HR
- Marketingu
 
https://www.ing.jobs/polska/`,
    name: "ING Polska",
    www: "https://www.ing.jobs/polska/",
    type: "silver",
    logo: ("/assets/partners/2023/ing.svg"),
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
    logo: ("/assets/partners/2023/allegro.svg"),
    orientation: "horizontal"
  },
  {
    id: "iqvia",
    description: `IQVIA jest wiodącym globalnym dostawcą zaawansowanych rozwiązań analitycznych i technologicznych oraz usług w zakresie badań klinicznych. Dzięki analizom, wykorzystaniu przełomowej technologii, zasobom big data i rozległej wiedzy branżowej, tworzymy inteligentne połączenia we wszystkich aspektach ochrony zdrowia. IQVIA Connected Intelligence™ w sposób szybki i zwinny dostarcza istotnych informacji, umożliwiając klientom przyspieszenie rozwoju klinicznego i komercjalizacji innowacyjnych metod terapeutycznych, które poprawiają wyniki leczenia pacjentów. Zatrudniając około 85 000 pracowników, IQVIA prowadzi działalność w ponad 100 krajach.

IQVIA is a leading global provider of advanced analytics, technology solutions, and clinical research services to the life sciences industry. IQVIA creates intelligent connections across all aspects of healthcare through its analytics, transformative technology, big data resources and extensive domain expertise. IQVIA Connected Intelligence™ delivers powerful insights with speed and agility — enabling customers to accelerate the clinical development and commercialization of innovative medical treatments that improve healthcare outcomes for patients. With approximately 85,000 employees, IQVIA conducts operations in more than 100 countries.`,
    name: "IQVIA",
    www: "https://www.iqvia.com/",
    type: "silver",
    logo: ("/assets/partners/2023/iqvia.svg"),
    orientation: "horizontal"
  },
  {
    id: "nordea",
    name: "Nordea",
    description: `Nordea Technology zatrudnia ponad 8000 osób o różnym pochodzeniu, narodowości i kompetencjach, którzy pracują ramię w ramię niezależnie od lokalizacji. Core development team znajduje się w Warszawie i Trójmieście. Rozbudowujemy również zespoły IT w Łodzi. Nordea Technology odpowiada za implementację rozwiązań IT dla całego banku zgodnie z obowiązującymi regulacjami systemów bezpieczeństwa.

[facebook.com/KarierawNordea/](https://www.facebook.com/KarierawNordea/)

[instagram.com/nordea_polska](http://www.instagram.com/nordea_polska)

[nordea.com](https://www.nordea.com/)`,
    www: "https://www.nordea.com/",
    type: "silver",
    logo: ("/assets/partners/2023/nordea.svg"),
    orientation: "horizontal"
  },
  {
    id: "bnpparibas",
    name: "BNP Paribas",
    description: `Wprowadzamy pozytywną bankowość w życie naszych Klientów, odpowiadając na ich potrzeby finansowe i ułatwiając im realizację ich celów. Działamy w sposób prosty, przemyślany i bezpieczny, dbając o społeczeństwo i środowisko. 

Na Confitura pojawiamy się nie tylko jako bank, ale przede wszystkim jako pracodawca z obszaru IT, bo obszar Nowych Technologii i Cyberbezpieczeństwa jest kluczowy w funkcjonowaniu naszego biznesu. Bankowość to nie tylko obszary sprzedaży czy operacji. Bank BNP Paribas to również miejsce, w którym pracownicy i pracowniczki mogą być odpowiedzialni za rozwój nowoczesnych aplikacji internetowych i mobilnych, budowanie innowacyjnych rozwiązań z wykorzystaniem Big Data, Cloud oraz AI, modernizację i utrzymanie infrastruktury, wsparcie użytkowników i wiele innych.

Transformujemy się i wprowadzamy nieustannie nowe technologie by jeszcze lepiej i sprawniej obsługiwać naszych Klientów i dostarczać im najwyższą jakość usług i produktów.

Możesz liczyć na:
- Dołączenie do banku o globalnym zasięgu, który jest częścią grupy bankowej BNP Paribas obecnej w 65 krajach z 200-letnią tradycją,
- Pracę w miejscu, w którym możesz być sobą niezależnie od swojej orientacji seksualnej, tożsamości płciowej, koloru skóry, pochodzenia czy poziomu sprawności,
- Rozwój w Banku Zielonych Zmian, w którym możesz mieć wpływ na naszą planetę i otaczający nas świat,
- Dużą autonomię w działaniu przy jednoczesnym wsparciu przełożonego/przełożonej,
- Możliwość podejmowania własnych decyzji, eksperymentowania i tworzenia swojej unikalnej ścieżki doświadczeń,
- Pracę w środowisku agile’owym, które wspiera elastyczność i innowacyjność organizacji, w końcu jesteśmy Bankiem zmieniającego się świata.

Razem zobowiązaliśmy się być liderem zrównoważonego finansowania.

#tumożesz https://www.bnpparibas.pl`,
    www: "https://www.bnpparibas.pl",
    type: "silver",
    logo: ("/assets/partners/2023/bnpparibas.svg"),
    orientation: "horizontal"
  },
  {
    id: "decerto",
    name: "Decerto",
    description: `Jesteśmy polskim software house’m obecnym na rynku od 2006 roku. Realizujemy projekty dla dużych korporacji, głównie z sektora finansowego. Zachowujemy płaską strukturę organizacyjną i niekorporacyjną kulturę pracy. Dzięki zaufaniu Klientów prowadzimy kilkanaście dużych projektów. Posiadamy duże doświadczenie i know-how, a we współpracy stawiamy na długoterminowe relacje. Pracując z nami, zyskujesz stabilność zatrudnienia, rozwój oraz możliwość poznania świata finansów i ubezpieczeń. Specjalizujemy się w rozwiązaniach backendowych związanych z dużymi wolumenami danych oraz logiką biznesową. Projektujemy kompleksowe systemy informatyczne, dlatego cenimy otwarty umysł, własną inicjatywę oraz samodzielność.

[www.decerto.com](https://www.decerto.com/pl/kariera)
`,
    www: " https://www.decerto.com/pl/kariera",
    type: "silver",
    logo: ("/assets/partners/2023/decerto.svg"),
    orientation: "horizontal"
  },
  {
    id: "softwaremill",
    name: "SoftwareMill",
    description: `Hello from [SoftwareMill](https://www.softwaremill.com/) - a consulting & custom software agency that proactively helps businesses benefit from technologies like Cloud Computing, Big Data, Stream Processing and Machine Learning.

We specialise in everything that helps us build high-performing distributed systems. From Java, Scala, Python, TypeScript and React, through Akka, the Lightbend Reactive Platform, Kafka, Spark, and various Databases, to tools that enable us to improve developers' experience with Platform Engineering: Kubernetes, Docker, AWS, GCP and Azure.

We build on the traditional engineering work ethics and values, which translate directly to the quality of our systems. We are a leading consultancy chosen for digital transformation, recognized from our integrity, versatility, understanding of the business, right soft skills, strong work ethic, rich experience and top notch mastery of technology.

Widening technical horizons is the fabric of our organisation. Not only do we practise and learn constantly, but we also teach, create and contribute. We have a proven track record of working with the IT community, leading open-source projects [[why not to contribute?](https://github.com/softwaremill/)] and organising a [Scalar conference](https://scalar-conf.com/).

Our priority is to deliver quality software: thoroughly tested, well-designed, and reviewed. But, our growth is not limited to technology. Since 2009 we have been building the best, fully remote, workplace for developers. We have a unique, 100% remote and bottom-up structure where leaders are not appointed, but chosen by teammates. In 2022, we’ve grown into a company of 100+ people. We simply like each other and together we create [a friendly environment](https://softwaremill.com/about-us/) that enhances our potential.

We’re eager to welcome more passionate software engineers to our Team. If you want to share your knowledge, empower teammates and lead by example, while working on interesting projects, check out our [career page](https://softwaremill.com/join-us/)!

Catch #SoftwareMillVibes:

[SoftwareMill Tech Blog](https://softwaremill.com/blog/)

[Scala Times newsletter](https://scalatimes.com/)

[Scala 3 Tech Report](https://softwaremill.com/ebooks/scala-3-tech-report/)

[SoftwareMill Academy](https://academy.softwaremill.com/en/)

[AMA Slack](https://sml.io/ask-me-anything)

[Instagram](https://instagram.com/softwaremill_vibes)

[Youtube](https://www.youtube.com/c/SoftwareMillCom)

[Facebook](https://www.facebook.com/softwaremill)

[Twitter](https://twitter.com/softwaremill)

[Linkedin](https://www.linkedin.com/company/808422)

[Mastodon](https://softwaremill.social/@softwaremill/)

[DevSkin Softwear](https://devskin-softwaremill.myspreadshop.pl/)
`,
    www: "https://softwaremill.com/",
    type: "silver",
    logo: ("/assets/partners/2023/sml.svg"),
    orientation: "horizontal"
  },
  {
    id: "vodeno",
    name: "Vodeno",
    description: `Hi, we are Vodeno!

We are transforming the financial services industry. We offer instant access to the most comprehensive banking as a service platform available in the market, addressing the challenges of increased regulation and exploiting the digital revolution through innovation.

Our Engineers built Vodeno Cloud Platform. It is the most comprehensive, ready to use solution for Retail & SME Banking enabled by advanced API technology and the world's first banking platform based entirely on Google Cloud. The platform complies with strict regulations and allows innovation at speed. The Platform is a unique composition of micro-services operating within a scalable lambda architecture and with native support of big data processing. Being fully cloud-based and API-enabled, our platform simplifies migration from any legacy core banking systems or other in-house solutions. This is what we call pioneering!

Each of us brings different experiences and knowledge to Vodeno. Together we create a team of people who have an enormous drive to explore the unknown. To pioneer and be ready to test and learn. To fail forward. To innovate. To change things for the better.

Get to know us at [www.vodeno.com/careers](https://www.vodeno.com/careers)`,
    www: "https://www.vodeno.com/careers",
    type: "silver",
    logo: ("/assets/partners/2023/vodeno.svg"),
    orientation: "horizontal"
  },
  {
    id: "onwelo",
    name: "Onwelo",
    description: `We are one of the fastest-growing technology companies in the Polish market, delivering modern IT solutions that shape the digital growth of organizations from many industries around the world. Our main areas of operations are: business analysis, software development and maintenance, software testing, IT infrastructure management, development of Data & Analytics and Intelligent Automation solutions, delivering cloud-based products and services. Figures confirm our dynamic growth. In a short time, we have completed over 300 projects in Europe and the USA, enlarged our team to over 700 experts, and opened offices in seven Polish cities and also branches in the United States, Germany and Switzerland. Our success is the work of a team of experienced IT experts. We strive to align employee skills with customer requirements in such a way as to support business growth of international brands while also enabling our experts to participate in challenging projects using state-of-the-art technologies.

Get to know us better!

[onwelo.com](https://onwelo.com/career/)`,
    www: "https://onwelo.com/career/",
    type: "silver",
    orientation: "horizontal",
    logo: ("/assets/partners/2023/onwelo.svg")
  },
  {
    id: "nussknacker",
    name: "Nussknacker",
    description: `Nussknacker to produkt stworzony i rozwijany przez developerów z TouK. Jest to narzędzie low-code, które umożliwia tworzenie i uruchamianie scenariuszy przetwarzania danych w czasie rzeczywistym. Podstawowym wejściem są dla Nussknackera strumienie (Kafka), ale w zależności od potrzeb może też działać w trybie synchronicznym i wsadowym. Dane wejściowe mogą być z kolei wzbogacane o dane z baz danych, API czy modeli ML. Nu wykorzystywany jest między innymi do wykrywania fraudów, tworzenia akcji marketingowych, wspomagania decyzji sprzedażowych oraz sprawdzania wiarygodności klientów zarówno w Polsce jak i za granicą.

 Podstawowa wersja Nu to open source, ale jednocześnie rozwijamy Nu Cloud, czyli Nussknackera dostępnego w chmurze.

Wykorzystujemy technologie takie jak: Scala, Kafka, Flink, Java, Kubernetes, Kotlin i React.

Jeżeli nie przeraża Cię praca w złożonym ekosystemie, a wręcz czujesz nutkę ekscytacji na myśl o eksploracji, chcesz się uczyć Scali i Kotlina, podoba Ci się projekt, w którym rzadko trafia się na podobne zadanie, chodź rozwijać i wdrażać Nussknackera i podbijać międzynarodowe rynki razem z nami ;)

[nussknacker.io](https://nussknacker.io/)

[Twitter](https://twitter.com/Nussknacker_io)

[LinkedIn](https://www.linkedin.com/company/nussknacker/)
`,
    www: "https://nussknacker.io/",
    type: "silver",
    orientation: "horizontal",
    logo: ("/assets/partners/2023/nussknacker.svg")
  },
  {
    id: "match-trade",
    name: "Match-trade",
    description: `Jesteśmy firmą z branży fintech, tworzącą zaawansowane systemy transakcyjne dla rynku Forex, kontraktów CFD, kryptowalut oraz akcji. Tworzymy własne środowisko technologiczne i jesteśmy autorem platformy tradingowej opartej o technologię PWA. 
  
  Współpracujemy z klientami z całego świata - w tym największymi brokerami Forex i najbardziej znanymi giełdami kryptowalut. W ramach jednego z naszych głównych projektów, zajmujemy się budową algorytmów z zakresu High Frequency Trading. Algorytmy te każdego dnia obracają kryptowalutami wartymi miliony dolarów.
  
  Nasze rozwiązania są wykorzystywane na giełdach z aż pięciu kontynentów. Członkowie naszego zespołu, nierzadko pasjonaci technologii blockchain oraz kryptowalut, każdego dnia zapoznają się z nowinkami tej nowej, dynamicznie rozwijającej się części sektora finansowego.
  
  Śmiało możemy powiedzieć też, szeroki zakres oferowanych przez nas usług stwarza dla naszych pracowników możliwości uczestniczenia w różnorodnych projektach, nie tylko dzięki wykorzystaniu różnych technologii (w tym nowoczesnej technologii Blockchain), ale także dzięki pracy z biznesami z całego świata.
  
  https://praca.match-trade.com/`,
    www: "https://praca.match-trade.com/",
    type: "silver",
    orientation: "horizontal",
    logo: ("/assets/partners/2023/match-trade.svg")
  },
  {
    id: "jit-team",
    name: "Jit team",
    description: `Jit Team – the human factor of IT.

We are a team full of teams! We build software development teams for our clients operating in various branches of business. We believe that the most important factor, even when technology is concerned, is the human factor. Our main value are JIT people, who are excellent specialists.

We are transparent and honest to ensure the cooperation with our clients and employees is successful and long-term.

https://jit.team
`,
    www: "https://jit.team",
    type: "silver",
    orientation: "horizontal",
    logo: ("/assets/partners/2023/jit-team.svg")
  },
  {
    id: "grid-dynamics",
    name: "Grid Dynamics",
    description: `Grid Dynamics is an engineering services company known for transformative, mission-critical cloud solutions for the retail, finance, and technology sectors. We have architected some of the busiest e-commerce services on the Internet, and have never had an outage during the peak season. Founded in 2006 and headquartered in San Ramon, California with offices throughout the US and Eastern Europe, we focus on big data analytics, scalable omnichannel services, DevOps, and cloud enablement.
As of today, Grid Dynamics brings together more than 4,000 engineers in 13 countries (USA, Mexico, Jamaica, Netherlands, England, Switzerland, Romania, Serbia, Armenia, Ukraine, Poland, Moldova, India) and continues to grow steadily. 

Check our open positions here: [https://careers.griddynamics.com/](https://careers.griddynamics.com/?utm_source=confitura)

Follow us here:
 - [Instagram](https://www.instagram.com/griddynamics_global)
 - [Facebook](https://www.facebook.com/griddynamicsglobal)
 - [LinkedIn](https://www.linkedin.com/company/grid-dynamics)
`,
    www: "http://careers.griddynamics.com",
    type: "silver",
    orientation: "horizontal",
    logo: ("/assets/partners/2023/griddynamics.svg")
  },
  {
    id: "paramount",
    description: `Paramount Global delivers premium content to audiences across platforms worldwide. Through our studios, networks, streaming services, live events, merchandise, we connect with billions of people. Our studios create content for all audiences, across every genre and format, while our networks and brands forge deep connections with the world’s most diverse audiences. In streaming, our differentiated strategy is scaling rapidly across free, broad pay, and premium.
 
Paramount in Poland plays a crucial role in the global engineering organization, ensuring worldwide access to Paramount content through SVOD, TVOD, and AVOD services. Our projects cover content and delivery platforms, user management, personalization, event collection, and solutions for emerging platforms. Our talented teams are essential to achieving our mission of Entertaining The Planet by attracting and retaining top tech talent.
 
Our teams are constantly growing, and we are always on a look out for new talents to join Paramount. If you want a chance to decide if Paramount is a place where you want to be – apply! We will be more than happy to get to know you and to tell you more about opportunities here in our company!

https://www.paramount.com/`,
    name: "Paramount",
    www: "https://www.paramount.com//",
    type: "gold",
    logo: ("/assets/partners/2023/paramount.svg"),
    orientation: "horizontal"
  },
  {
    id: "j-labs",
    name: "j-labs",
    description: `Created by engineers for engineers – we are j-labs software specialist!
Our motto is "Code matters. YOU more!" - that’s why j-labs gathers the best talents with an average of nine years of work experience, giving them the opportunity to use and develop their skills. How? By providing the highest level of software development to their clients. Over 500 people work in j-labs branches in Krakow and Warsaw.
We do good IT, or not at all!`,
    www: "https://www.j-labs.pl",
    type: "silver",
    logo: ("/assets/partners/2023/jlabs.svg"),
    orientation: "horizontal"
  },
  {
    id: "coi",
    name: "Centralny Ośrodek Informatyki",
    description: `Centralny Ośrodek Informatyki is the largest polish company that develops IT projects for the public sector. You may know COI projects such as mObywatel or Profil Zaufany. Company also created the "COI for Ukraine" project, which introduced a comprehensive PESEL registration system for Ukrainian citizens.

As you can see, COI projects are of great importance. You can also be a part of them and create solutions that make life easier for millions of people! COI is constantly working on new projects, which means that it’s always recruiting 🚀

COI is looking for experts in backend (mainly Java), frontend (Angular), mobile applications (Android, iOS), testing, analysis, IT architecture, security, UX/UI, and more!

Some of the advantages of working at COI include:

✅ work with the best experts on the largest systems in the country,

✅ huge opportunities for development and knowledge sharing (internal and external trainings, conferences, subsidized studies, promotion opportunities and possibility to change project),

✅ comfortable work (100% remote, flexible hours, modern office in Warsaw if you prefer office/hybrid work),

✅ a vast range of benefits (day off on birthdays, an additional four-weeks off after three years of work, vacation subsidies, bonuses)… and many more!

Visit COI booth during Confitura and learn more about their career opportunities! 💥`,
    www: "https://www.coi.gov.pl/",
    type: "silver",
    orientation: "horizontal",
    logo: ("/assets/partners/2023/coi.svg")
  },
  {
    id: "appfire",
    name: "Appfire",
    description: `Appfire is a leading provider of software that helps developers and teams solve modern challenges with digital solutions.

Since launching in 2005, Appfire has built a platform of 200+ trusted apps and top-selling solutions for more than 30,000 customers spanning 150 countries and all industry groups — including 55% of Fortune 500 companies. Our software is available as standalone products and through platforms such as Atlassian, Microsoft, Salesforce, Monday.com, and more. We support teams with Workflow Automation, Product Portfolio Management, IT Service Management, Document Management, Business Intelligence and Reporting, Administrative Tools, Agile, Developer Tools, and Publishing. With over 750 employees globally, Appfire prioritizes a flexible work model and human-focused perspective culture.

[www.appfire.com](https://www.appfire.com)
`,
    www: "https://www.appfire.com",
    type: "silver",
    logo: ("/assets/partners/2023/appfire.svg"),
    orientation: "horizontal"
  },
  {
    id: "luxoft",
    name: "Luxoft",
    description: `**Who we are**
                    
Luxoft, a DXC Technology Company is an international digital strategy and software engineering company. We have been operating in Poland since 2010. Luxoft is a digital transformation and software engineering services company that provides customized IT solutions that drive business change for customers in every corner of the globe. 

At Luxoft Poland, we strive to provide customers with tailor-made technological solutions. Furthermore, we combine high-quality services with in-depth industry knowledge. We specialize in automotive, financial services, media, telecommunications, and other sectors. Technology is our passion! We employ experienced specialists with whom we implement ambitious and long-term projects. Luxoft is a global family with an epic atmosphere - we love what we do! We are not slowing down, so we can confidently say that we are a stable employer even in difficult times. 

Luxoft Poland is well known for its consistently high level of supply and complex project management. Then, it is the talent of our highly qualified experts. We are operating in the field of digital engineering. Luxoft offers exceptional customer orientation as well as agility, creativity, and outstanding problem-solving capabilities.

[Career at Luxoft](https://career.luxoft.com/locations/poland/?utm_source=website&utm_medium=website&utm_campaign=20230624-b2e-eb-poland-confitura)`,
    www: "https://career.luxoft.com/locations/poland/?utm_source=website&utm_medium=website&utm_campaign=20230624-b2e-eb-poland-confitura",
    type: "silver",
    logo: ("/assets/partners/2023/luxoft.png"),
    orientation: "horizontal"
  },
  {
    id: "PROBrand",
    name: "PROBrand",
    description: `PRObrand: Brand New Event Technologies

Komfortowa obsługa techniczna targów, konferencji i kongresów w zakresie:

* Ekranów projekcyjnych, ekranów LED i telewizorów 43-98"

* Nagłośnienia

* Oświetlenia architektonicznego i scenicznego

* Osprzętu do tłumaczeń symultanicznych

Wspieramy przygotowanie wydarzenia na wszystkich jego etapach.

Zapraszamy także do naszej wypożyczalni sprzętu eventowego: [www.eventav.pl](www.eventav.pl)`,
    www: "https://www.facebook.com/PRObrand-Sp-z-oo-170774782953644/",
    type: "tech",
    logo: ("/assets/partners/2023/eventAV.svg"),
    orientation: "horizontal"
  },
];
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
    tech: ({partners}): Partner[] => filterBy(partners, "tech")
  },
  actions: {
    getPartnerById(id: string){
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

//TODO move to types
export interface Partner {
  id: string;
  name: string;
  description: string;
  logo: string;
  www: string;
  type:
    | ""
    | "platinum"
    | "path"
    | "gold"
    | "silver"
    | "bronze"
    | "media"
    | "tech";
  orientation?: "horizontal" | "vertical" | "box";
}