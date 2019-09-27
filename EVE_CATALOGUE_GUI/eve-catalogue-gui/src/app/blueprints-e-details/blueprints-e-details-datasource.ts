import { DataSource } from '@angular/cdk/collections';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { map } from 'rxjs/operators';
import { Observable, of as observableOf, merge } from 'rxjs';

// TODO: Replace this with your own data model type
/***
export interface BlueprintsEDetailsItem {
  expBlueprintId: string;
  expBlueprintVersion: string;
  name: string;
  activeExpdId: string[];
  onBoardedNsdInfoId: string[];
}
*/

export interface BlueprintsEDetailsItemKV {
  key: string;
  value: string[];
}

// TODO: replace this with real data from your application
/***
const EXAMPLE_DATA: BlueprintsEDetailsItem[] = [
  {expBlueprintId: "1", expBlueprintVersion: "v0.1", name: 'Hydrogen', activeExpdId: [], onBoardedNsdInfoId: ['VideoStreaming']}
];
*/


const EXAMPLE_DATAKV: BlueprintsEDetailsItemKV[] = [
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
export class BlueprintsEDetailsDataSource extends DataSource<BlueprintsEDetailsItemKV> {
  data: BlueprintsEDetailsItemKV[] = EXAMPLE_DATAKV;
  paginator: MatPaginator;
  sort: MatSort;

  constructor() {
    super();
  }

  /**
   * Connect this data source to the table. The table will only update when
   * the returned stream emits new items.
   * @returns A stream of the items to be rendered.
   */
  connect(): Observable<BlueprintsEDetailsItemKV[]> {
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
  private getPagedData(data: BlueprintsEDetailsItemKV[]) {
    const startIndex = this.paginator.pageIndex * this.paginator.pageSize;
    return data.splice(startIndex, this.paginator.pageSize);
  }

  /**
   * Sort the data (client-side). If you're using server-side sorting,
   * this would be replaced by requesting the appropriate data from the server.
   */
  private getSortedData(data: BlueprintsEDetailsItemKV[]) {
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
