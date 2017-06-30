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
import {IsPrivilegedGuard} from "./is-privileged-guard.service";
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
                canActivateChild: [IsPrivilegedGuard],
                children: [
                    {path: "scanner", component: ScannerComponent},
                    {path: "scanner/:id", component: ScannerComponent},
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
    providers: [IsAdminGuard, IsPrivilegedGuard]

})
export class AdminRoutingModule {

}
