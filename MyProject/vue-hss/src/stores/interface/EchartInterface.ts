export interface EchartVO {
  title: string;
  xAxis: string[];              // LocalDate 在前端一般是字符串，例如 "2025-04-05"
  yAxis: number[][];            // 对应多条折线，每条线的 y 轴数据
  seriesNames: string[];        // 每条折线的名称
}

