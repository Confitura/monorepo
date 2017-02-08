import {Routes, RouterModule} from "@angular/router";
import {ModuleWithProviders} from "@angular/core";
import {HomeComponent} from "./pages/home/home.component";
import {AboutComponent} from "./pages/about/about.component";
import {PartnersComponent} from "./pages/partners/partner-list/partners.component";
import {PartnerComponent} from "./pages/partners/partner/partner.component";
const appRoutes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'about', component: AboutComponent },
    { path: 'partners', component: PartnersComponent },
    { path: 'partners/:id', component: PartnerComponent },

];
export const routing: ModuleWithProviders = RouterModule.forRoot(appRoutes);