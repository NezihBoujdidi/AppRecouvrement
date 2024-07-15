import { NgModule, Component } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LogInComponent } from './log-in/log-in.component';
import { HomePageComponent } from './home-page/home-page.component';
import { AfaireComponent } from './afaire/afaire.component';
import { BalanceAgeeComponent } from './balance-agee/balance-agee.component';
import { ProfilClientComponent } from './profil-client/profil-client.component';
import { DetailsPaiementComponent } from './details-paiement/details-paiement.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LogInComponent },
  { path: 'home', component: HomePageComponent },
  { path: 'Afaire', component: AfaireComponent },
  { path: 'balanceAgee', component: BalanceAgeeComponent},
  { path: 'profilClient/:id', component: ProfilClientComponent},
  { path: 'payment-details', component: DetailsPaiementComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
