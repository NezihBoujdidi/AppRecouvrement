import { Injectable } from '@angular/core';
import { Client } from '../models/client';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  private apiUrl = 'http://localhost:8081/api/clients';

  constructor(private http: HttpClient) { }

  getAllClients(): Observable<Client[]> {
    return this.http.get<Client[]>(this.apiUrl);
  }

  getClientById(clientId: number): Observable<Client> {
    const url = `${this.apiUrl}/${clientId}`;
    return this.http.get<Client>(url);
  }

  createClient(client: Client): Observable<Client> {
    return this.http.post<Client>(this.apiUrl, client);
  }

  updateClient(clientId: number, clientDetails: Client): Observable<Client> {
    const url = `${this.apiUrl}/${clientId}`;
    return this.http.put<Client>(url, clientDetails);
  }

  deleteClient(clientId: number): Observable<void> {
    const url = `${this.apiUrl}/${clientId}`;
    return this.http.delete<void>(url);
  }
}
