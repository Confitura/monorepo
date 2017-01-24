import {Routes, RouterModule} from "@angular/router";
import {ModuleWithProviders} from "@angular/core";
import {HomeComponent} from "./pages/home/home.component";
const appRoutes: Routes = [
    { path: '', component: HomeComponent },

];
export const routing: ModuleWithProviders = RouterModule.forRoot(appRoutes);