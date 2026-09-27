import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CreditoService } from './services/credito.service';
import { Credito, TipoBusca } from './models/credito.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  tipoBusca = signal<TipoBusca>('NFSE');
  termoBusca = signal<string>('');
  carregando = signal<boolean>(false);
  erro = signal<string | null>(null);
  creditos = signal<Credito[]>([]);
  buscaRealizada = signal<boolean>(false);

  // Métricas computadas
  totalRegistros = computed(() => this.creditos().length);

  totalIssqn = computed(() => {
    return this.creditos().reduce((acc, c) => acc + (Number(c.valorIssqn) || 0), 0);
  });

  totalFaturado = computed(() => {
    return this.creditos().reduce((acc, c) => acc + (Number(c.valorFaturado) || 0), 0);
  });

  totalBaseCalculo = computed(() => {
    return this.creditos().reduce((acc, c) => acc + (Number(c.baseCalculo) || 0), 0);
  });

  constructor(private creditoService: CreditoService) {}

  setTipoBusca(tipo: TipoBusca): void {
    if (this.tipoBusca() !== tipo) {
      this.tipoBusca.set(tipo);
      this.termoBusca.set('');
      this.erro.set(null);
    }
  }

  preencherExemplo(tipo: TipoBusca, valor: string): void {
    this.tipoBusca.set(tipo);
    this.termoBusca.set(valor);
    this.consultar();
  }

  limpar(): void {
    this.termoBusca.set('');
    this.creditos.set([]);
    this.erro.set(null);
    this.buscaRealizada.set(false);
  }

  consultar(): void {
    const termo = this.termoBusca().trim();
    if (!termo) {
      this.erro.set('Por favor, informe o termo para pesquisa.');
      return;
    }

    this.carregando.set(true);
    this.erro.set(null);
    this.buscaRealizada.set(true);

    if (this.tipoBusca() === 'NFSE') {
      this.creditoService.buscarPorNfse(termo).subscribe({
        next: (data) => {
          this.creditos.set(data || []);
          this.carregando.set(false);
        },
        error: (err) => {
          this.carregando.set(false);
          this.creditos.set([]);
          if (err.status === 404) {
            this.erro.set(`Nenhum crédito constituído encontrado para a NFS-e "${termo}".`);
          } else {
            this.erro.set(err.error?.message || 'Erro ao conectar à API de créditos. Verifique se o backend está em execução.');
          }
        }
      });
    } else {
      this.creditoService.buscarPorNumeroCredito(termo).subscribe({
        next: (data) => {
          this.creditos.set(data ? [data] : []);
          this.carregando.set(false);
        },
        error: (err) => {
          this.carregando.set(false);
          this.creditos.set([]);
          if (err.status === 404) {
            this.erro.set(`Crédito constituído "${termo}" não foi encontrado no sistema.`);
          } else {
            this.erro.set(err.error?.message || 'Erro ao conectar à API de créditos. Verifique se o backend está em execução.');
          }
        }
      });
    }
  }

  formatarMoeda(valor: number | string | null | undefined): string {
    if (valor === null || valor === undefined || isNaN(Number(valor))) {
      return 'R$ 0,00';
    }
    return Number(valor).toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  }

  formatarPercentual(valor: number | string | null | undefined): string {
    if (valor === null || valor === undefined || isNaN(Number(valor))) {
      return '0,00%';
    }
    return Number(valor).toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }) + '%';
  }

  formatarData(dataStr: string | null | undefined): string {
    if (!dataStr) return '-';
    const partes = dataStr.split('-');
    if (partes.length === 3) {
      return `${partes[2]}/${partes[1]}/${partes[0]}`;
    }
    return dataStr;
  }
}
