import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {PartnerEditComponent} from "./partners/partner/partner-edit.component";
import {PartnerComponent} from "../pages/partners/partner/partner.component";
import {IsAdminGuard} from "./is-admin-guard.service";
import {LoginComponent} from "../pages/login/login.component";
import {UserListComponent} from "./users/user-list/user-list.component";
import {ParticipantListComponent} from "./participants/participant-list.component";
import {AgendaComponent} from "./agenda/agenda.component";
import {ScannerComponent} from "./scanner/scanner.component";
import {IsVolunteerGuard} from "./is-volunteer-guard.service";
const routes: Routes = [
    {
        path: "admin2",
        children: [
            {
                path: "",
                pathMatch: "full",
                component: LoginComponent,
            },
            {
                path: "",
                children: [
                    {path: "scanner", component: ScannerComponent, canActivate: [IsAdminGuard, IsVolunteerGuard]},
                    {path: "scanner/:id", component: ScannerComponent, canActivate: [IsAdminGuard]},
                    {path: "users", component: UserListComponent, canActivate: [IsAdminGuard]},
                    {path: "partners/add", component: PartnerEditComponent, canActivate: [IsAdminGuard]},
                    {path: "partners/edit/:id", component: PartnerEditComponent, canActivate: [IsAdminGuard]},
                    {path: "partners/:id", component: PartnerComponent, canActivate: [IsAdminGuard]},
                    {path: "agenda", component: AgendaComponent, canActivate: [IsAdminGuard]},
                    {path: "participants", component: ParticipantListComponent, canActivate: [IsAdminGuard]}]
            }
        ]
    }
];
@NgModule({
    imports: [RouterModule.forChild(routes)],
    providers: [IsAdminGuard, IsVolunteerGuard]

})
export class AdminRoutingModule {

}
