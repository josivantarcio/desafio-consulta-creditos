import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Credito } from '../models/credito.model';

@Injectable({
  providedIn: 'root'
})
export class CreditoService {

  private get baseUrl(): string {
    if (typeof window !== 'undefined' && window.location.port === '4200') {
      return 'http://localhost:8080/api/creditos';
    }
    return '/api/creditos';
  }

  constructor(private http: HttpClient) {}

  buscarPorNfse(numeroNfse: string): Observable<Credito[]> {
    const cleanNfse = numeroNfse.trim();
    return this.http.get<Credito[]>(`${this.baseUrl}/${encodeURIComponent(cleanNfse)}`);
  }

  buscarPorNumeroCredito(numeroCredito: string): Observable<Credito> {
    const cleanCredito = numeroCredito.trim();
    return this.http.get<Credito>(`${this.baseUrl}/credito/${encodeURIComponent(cleanCredito)}`);
  }
}
