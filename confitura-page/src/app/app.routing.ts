import {RouterModule, Routes} from "@angular/router";
import {ModuleWithProviders} from "@angular/core";
import {HomeComponent} from "./pages/home/home.component";
import {AboutComponent} from "./pages/about/about.component";
import {PartnersComponent} from "./pages/partners/partner-list/partners.component";
import {PartnerComponent} from "./pages/partners/partner/partner.component";
import {LoginComponent} from "./pages/login/login.component";
import {PresentationListComponent} from "./pages/presentations/presentation-list/presentation-list.component";
import {SpeakerListComponent} from "./pages/speakers/speaker-list.component";
import {AgendaComponent} from "./pages/agenda/agenda.component";
const appRoutes: Routes = [
    {path: '', component: HomeComponent},
    {path: 'about', component: AboutComponent},
    {path: 'presentations', component: PresentationListComponent},
    {path: 'speakers', component: SpeakerListComponent},
    {path: 'schedule', component: AgendaComponent},
    {path: 'partners', component: PartnersComponent},
    {path: 'partners/:id', component: PartnerComponent},
    {path: 'login', component: LoginComponent},
    {path: 'login/:origin', component: LoginComponent},

];
export const routing: ModuleWithProviders = RouterModule.forRoot(appRoutes);
