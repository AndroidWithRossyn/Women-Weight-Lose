package com.charting.interfaces.dataprovider;

import com.charting.components.YAxis.AxisDependency;
import com.charting.data.BarLineScatterCandleBubbleData;
import com.charting.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    boolean isInverted(AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
