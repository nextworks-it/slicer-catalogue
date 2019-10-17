import { DataSource } from '@angular/cdk/collections';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { map } from 'rxjs/operators';
import { Observable, of as observableOf, merge } from 'rxjs';

export interface BlueprintsVsDetailsItemKV {
  key: string;
  value: string[];
}

const EXAMPLE_DATAKV: BlueprintsVsDetailsItemKV[] = [
  {key: "Id", value: ["6"]},
  {key: "Name", value: ["Blueprint for AGV experiments in a factory with variable size"]},
  {key: "Version", value: ["1.0"]},
  {key: "Description", value: ["Blueprint for AGV experiments in a factory with variable size"]},
  {key: "Sites", value: ['5Tonic/Madrid']},
  {key: "KPIs", value: ['Navigation Level', 'Consumption', 'Time to loose guide', 'Delay']},
  {key: "Infrastructure Metrics", value: ['Latency', 'Downlink Bandwidth', 'Uplink Bandwidth']},
  {key: "VS Blueprint Name", value: ["ASTI AGV control and automation"]},
  {key: "Context Blueprint Name", value: ["Delay configurator", "Traffic generator"]},
  {key: "Test Case Blueprint Name", value: ["AGV Navigation at different latencies", "Time to loose guide at different latencies"]},
  {key: "Linked Experiment Descriptors", value: ['ASTI_3AGV_5TONIC_small_factory', 'ASTI_10AGV_medium_factory']}
];

/**
 * Data source for the BlueprintsE view. This class should
 * encapsulate all logic for fetching and manipulating the displayed data
 * (including sorting, pagination, and filtering).
 */
export class VsbGraphDataSource extends DataSource<BlueprintsVsDetailsItemKV> {
  data: BlueprintsVsDetailsItemKV[] = [] /*EXAMPLE_DATAKV*/;
  paginator: MatPaginator;
  sort: MatSort;

  constructor(data: BlueprintsVsDetailsItemKV[]) {
    super();
    this.data = data;
  }

  /**
   * Connect this data source to the table. The table will only update when
   * the returned stream emits new items.
   * @returns A stream of the items to be rendered.
   */
  connect(): Observable<BlueprintsVsDetailsItemKV[]> {
    // Combine everything that affects the rendered data into one update
    // stream for the data-table to consume.
    const dataMutations = [
      observableOf(this.data),
      this.paginator.page,
      this.sort.sortChange
    ];

    return merge(...dataMutations).pipe(map(() => {
      return this.getPagedData(this.getSortedData([...this.data]));
    }));
  }

  /**
   *  Called when the table is being destroyed. Use this function, to clean up
   * any open connections or free any held resources that were set up during connect.
   */
  disconnect() {}

  /**
   * Paginate the data (client-side). If you're using server-side pagination,
   * this would be replaced by requesting the appropriate data from the server.
   */
  private getPagedData(data: BlueprintsVsDetailsItemKV[]) {
    const startIndex = this.paginator.pageIndex * this.paginator.pageSize;
    return data.splice(startIndex, this.paginator.pageSize);
  }

  /**
   * Sort the data (client-side). If you're using server-side sorting,
   * this would be replaced by requesting the appropriate data from the server.
   */
  private getSortedData(data: BlueprintsVsDetailsItemKV[]) {
    if (!this.sort.active || this.sort.direction === '') {
      return data;
    }

    return data.sort((a, b) => {
      const isAsc = this.sort.direction === 'asc';
      switch (this.sort.active) {
          case 'key':  return compare(a.key, b.key, isAsc);
        /*case 'name': return compare(a.name, b.name, isAsc);
          case 'id': return compare(+a.expBlueprintId, +b.expBlueprintId, isAsc);
        */
	default: return 0;
      }
    });
  }
}

/** Simple sort comparator for example ID/Name columns (for client-side sorting). */
function compare(a, b, isAsc) {
  return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}
