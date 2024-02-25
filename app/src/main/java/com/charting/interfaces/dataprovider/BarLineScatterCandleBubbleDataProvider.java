package com.charting.interfaces.dataprovider;

import com.charting.data.BarLineScatterCandleBubbleData;
import com.charting.utils.Transformer;
import com.charting.components.YAxis.AxisDependency;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    boolean isInverted(AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
